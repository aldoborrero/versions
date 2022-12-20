package io.accur8.neodeploy

import a8.shared.{CompanionGen, FileSystem}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.sync.RowSync._
import io.accur8.neodeploy.resolvedmodel.ResolvedUser
import zio.{Task, ZIO}
import PredefAssist._
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel._

object ManagedSshKeysSync extends Sync[ResolvedUser] with LoggingF {

  override val name: Sync.SyncName = Sync.SyncName("managed_ssh_keys")

  override def systemState(user: ResolvedUser): M[SystemState] = {
    val publicKey = user.sshPublicKeyFileInHome
    val privateKey = user.sshPrivateKeyFileInHome
    for {
      publicKeyContents <- user.sshPublicKeyFileInRepo.readAsString
      privateKeyContents <- user.sshPrivateKeyFileInRepo.readAsString
    } yield
      SystemState.Composite(
        "managed ssh keys",
        Vector(
          SystemState.Directory(publicKey.parent, UnixPerms("0700")),
          SystemState.TextFile(publicKey, publicKeyContents, UnixPerms("0644")),
          SystemState.SecretsTextFile(privateKey, SecretContent(privateKeyContents), UnixPerms("0600")),
        )
      )
  }


}

