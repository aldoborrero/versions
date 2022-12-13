package io.accur8.neodeploy.systemstate


import a8.shared.ZFileSystem
import io.accur8.neodeploy.systemstate.SystemStateModel._
import a8.shared.SharedImports._

trait TextFileContentsMixin extends SystemStateMixin {

  val filename: String
  val perms: UnixPerms

  def contents: String

  def prefix: String

  override def stateKey: Option[StateKey] = StateKey(filename).some

  override def dryRun: Vector[String] = Vector(s"${prefix}file ${filename} with perms ${perms}")

  override def isActionNeeded = {
    val file = ZFileSystem.file(filename)
    for {
      permissionActionNeeded0 <- SystemStateImpl.permissionsActionNeeded(file, perms)
      actualContentsOpt <- file.readAsStringOpt
    } yield permissionActionNeeded0 || (actualContentsOpt !== some(contents))
  }


  override def runApplyNewState = {
    val file = ZFileSystem.file(filename)
    for {
      parentExists <- file.parent.exists
      _ <-
        if (parentExists)
          zunit
        else
          file.parent.makeDirectories
      _ <- file.write(contents)
    } yield ()
  }


  override def runUninstallObsolete =
    ZFileSystem.file(filename).delete

}
