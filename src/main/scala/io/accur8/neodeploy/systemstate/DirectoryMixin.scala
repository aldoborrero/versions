package io.accur8.neodeploy.systemstate

import a8.shared.SharedImports._
import a8.shared.ZFileSystem
import io.accur8.neodeploy.systemstate.SystemStateModel._

trait DirectoryMixin extends SystemStateMixin { self: SystemState.Directory =>

  override def stateKey: Option[StateKey] = StateKey(path).some
  override def dryRun: Vector[String] = Vector(s"directory ${path} with ${perms}")

  override def isActionNeeded = {
    val dir = ZFileSystem.dir(path)
    SystemStateImpl.permissionsActionNeeded(dir, perms)
  }

  override def runApplyNewState = {
    val dir = ZFileSystem.dir(path)
    for {
      exists <- dir.exists
      _ <-
        if ( exists ) {
          zunit
        } else {
          dir.makeDirectories
        }
      _ <- SystemStateImpl.applyPermissions(dir, perms)
    } yield ()
  }

  override def runUninstallObsolete = {
    val dir = ZFileSystem.dir(path)
    for {
      entries <- dir.entries
      _ <-
        if (entries.isEmpty) {
          dir.delete
        } else {
          zunit
        }
    } yield ()
  }

}
