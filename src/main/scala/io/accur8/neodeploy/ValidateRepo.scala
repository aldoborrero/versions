package io.accur8.neodeploy


import a8.shared.app.{BootstrappedIOApp, LoggingF}
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import io.accur8.neodeploy.resolvedmodel.ResolvedRepository
import PredefAssist._

case class ValidateRepo(resolvedRepository: ResolvedRepository) extends LoggingF {

  lazy val gitRootDirectory = resolvedRepository.gitRootDirectory.resolvedDirectory

  lazy val allUsers =
    resolvedRepository
      .allUsers

  def run =
    setupSshKeys zipPar addGitattributesFile

  def setupSshKeys: Task[Unit] = {
    allUsers
      .filter(user => user.descriptor.manageSshKeys && !user.sshPrivateKeyFileInRepo.exists())
      .map { user =>
        val tempFile = user.tempSshPrivateKeyFileInRepo
        Command(
          "ssh-keygen", "-t", "ed25519", "-a", "100", "-f", z"$tempFile", "-q", "-N", "", "-C", user.qname
        )
          .workingDirectory(user.repoDir)
          .execLogOutput
          .asZIO(
            ZIO.attemptBlocking(
              tempFile.asNioPath.toFile.renameTo(user.sshPrivateKeyFileInRepo.asNioPath.toFile)
            )
          )
      }
      .sequencePar
      .as(())
  }

  def addGitattributesFile: Task[Unit] =
    ZIO.attemptBlocking {
      val gitAttributesFile = gitRootDirectory.file(".gitattributes")
      if ( !gitAttributesFile.exists() ) {
        val lines =
          Seq(
            ".gitattributes !filter !diff",
            "**/ed25519.priv filter=git-crypt diff=git-crypt",
          )
        gitAttributesFile.write(lines.mkString("", "\n", "\n"))
      }
    }

}
