package io.accur8.neodeploy

import a8.shared.{CompanionGen, FileSystem}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.sync.RowSync._
import io.accur8.neodeploy.ManagedSshKeysSync.State
import io.accur8.neodeploy.MxManagedSshKeysSync.MxState
import io.accur8.neodeploy.Sync.Phase
import io.accur8.neodeploy.resolvedmodel.ResolvedUser
import zio.{Task, ZIO}
import PredefAssist._
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel.UnixPerms

object ManagedSshKeysSync {

  object State extends MxState
  @CompanionGen
  case class State(
    publicKeyValue: String,
    publicKeyFile: String,
    privateKeyFile: String,
  )

}

class ManagedSshKeysSync extends Sync[State, ResolvedUser] with LoggingF {

  override val name: Sync.SyncName = Sync.SyncName("managed_ssh_keys")

  override def state(input: ResolvedUser): Task[Option[State]] =
    ZIO.attemptBlocking(
      input
        .descriptor
        .manageSshKeys
        .toOptionUnit
        .flatMap( _ =>
          input
            .sshPublicKeyFileInRepo
            .readAsStringOpt()
            .map { pubKeyValue =>
              State(
                publicKeyValue = pubKeyValue,
                publicKeyFile = z"${input.sshPublicKeyFileInHome}",
                privateKeyFile = z"${input.sshPrivateKeyFileInHome}",
              )
            }
        )
    )

  override def resolveStepsFromModification(modification: Sync.Modification[State, ResolvedUser]): Vector[Sync.Step] = {

    def updateKeys(user: ResolvedUser): Vector[Sync.Step] = {
      Vector(
        Sync.Step.copyFile(user.sshPublicKeyFileInRepo, user.sshPublicKeyFileInHome),
        Sync.Step.chmod("0644", user.sshPublicKeyFileInHome),
        Sync.Step.copyFile(user.sshPrivateKeyFileInRepo, user.sshPrivateKeyFileInHome),
        Sync.Step.chmod("0600", user.sshPrivateKeyFileInHome),
        Sync.Step.chmod("0700", user.sshPrivateKeyFileInHome.parent),
      )
    }

    modification match {
      case Sync.Delete(currentState) =>
        Vector(
          Sync.Step.deleteFile(FileSystem.file(currentState.publicKeyFile)),
          Sync.Step.deleteFile(FileSystem.file(currentState.privateKeyFile))
        )
      case Sync.Insert(newState, newInput) =>
        updateKeys(newInput)
      case Sync.Update(_, newState, newInput) =>
        updateKeys(newInput)
    }
  }

  /**
   * ??? TODO lift this into IO monad
   */
  override def rawSystemState(user: ResolvedUser): SystemState = {
    val publicKey = user.sshPublicKeyFileInHome
    val privateKey = user.sshPublicKeyFileInHome
    SystemState.Composite(
      "authorized keys 2",
      Vector(
        SystemState.Directory(publicKey.parent.absolutePath, UnixPerms("0700")),
        SystemState.TextFile(publicKey.absolutePath, publicKey.readAsString(), UnixPerms("0644")),
        SystemState.TextFile(privateKey.absolutePath, privateKey.readAsString(), UnixPerms("0600")),
      )
    )
  }


}

