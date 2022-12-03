package io.accur8.neodeploy


import a8.shared.FileSystem.file
import zio.Task
import a8.shared.SharedImports._
import a8.shared.app.Logging
import a8.shared.{FileSystem, ZString}
import com.softwaremill.sttp.Uri
import io.accur8.neodeploy.model.{AuthorizedKey, UserDescriptor}
import io.accur8.neodeploy.resolvedmodel.ResolvedUser

object AuthorizedKeys2Sync extends ConfigFileSync[ResolvedUser] with Logging {

  override val perms = "0644".some

  override val name: Sync.SyncName = Sync.SyncName("authorized_keys2")

  override def configFile(input: ResolvedUser): FileSystem.File =
    input.home.subdir(".ssh").file("authorized_keys2")

  override def configFileContents(input: ResolvedUser): Task[Option[String]] =
    zsucceed(
      input
        .resolvedAuthorizedKeys
        .map(_.value)
        .mkString("\n")
        .some
    )

  override def resolveStepsFromModification(modification: Sync.Modification[ConfigFileSync.State, ResolvedUser]): Vector[Sync.Step] = {
    def setSshDirPermsStep(user: ResolvedUser): Option[Sync.Step] =
      Sync.Step.chmod("0700", user.home.subdir(".ssh")).some

    val setSshDirPerms =
      modification match {
        case Sync.Update(_, _, user) =>
          setSshDirPermsStep(user)
        case Sync.Insert(_, user) =>
          setSshDirPermsStep(user)
        case _ =>
          None
      }

    super.resolveStepsFromModification(modification) ++ setSshDirPerms

  }
}

