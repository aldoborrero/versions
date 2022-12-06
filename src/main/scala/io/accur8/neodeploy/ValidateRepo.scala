package io.accur8.neodeploy


import a8.shared.app.{BootstrappedIOApp, LoggingF}
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import io.accur8.neodeploy.resolvedmodel.ResolvedRepository
import PredefAssist._

object ValidateRepo extends BootstrappedIOApp {

  lazy val resolvedRepository = LocalUserSyncSubCommand(Vector.empty, Vector.empty).resolvedRepository

  lazy val validateRepo = ValidateRepo(resolvedRepository)

  override def runT: ZIO[BootstrapEnv, Throwable, Unit] =
    validateRepo.run
  
}

case class ValidateRepo(resolvedRepository: ResolvedRepository) extends LoggingF {

  lazy val gitRootDirectory = resolvedRepository.gitRootDirectory.resolvedDirectory

  lazy val allUsers =
    resolvedRepository
      .allUsers

  def run =
    setupSshKeys zipPar addGitattributesFile

  def setupSshKeys: Task[Unit] = {
    val setupEffects =
      allUsers
        .filter { user =>
          val pkf = user.sshPrivateKeyFileInRepo
          val pkfExists = pkf.exists()
          user.descriptor.manageSshKeys && !pkfExists
        }
        .map { user =>
          val tempFile = user.tempSshPrivateKeyFileInRepo
          val makeDirectoriesEffect = ZIO.attemptBlocking(user.repoDir.makeDirectories())
          val sshKeygenEffect =
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
          (makeDirectoriesEffect *> sshKeygenEffect)
            .correlateWith0(s"setup ssh keys for ${user.qname}")
        }
    ZIO.collectAllPar(setupEffects)
      .as(())
  }

  def addGitattributesFile: Task[Unit] =
    ZIO.attemptBlocking {
      val gitAttributesFile = gitRootDirectory.file(".gitattributes")
      if ( !gitAttributesFile.exists() ) {
        val lines =
          Seq(
            ".gitattributes !filter !diff",
            "**/*.priv filter=git-crypt diff=git-crypt",
            "*.priv filter=git-crypt diff=git-crypt",
          )
        gitAttributesFile.write(lines.mkString("", "\n", "\n"))
      }
    }



}
