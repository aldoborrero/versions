package io.accur8.neodeploy


import a8.shared.SharedImports._
import a8.shared.FileSystem
import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import io.accur8.neodeploy.model.{AppsRootDirectory, CaddyDirectory, DomainName, GitServerDirectory, SupervisorDirectory}
import zio.ZIO

object Main extends BootstrappedIOApp {

  lazy val configFile =
    FileSystem
      .userHome
      .subdir(".a8")
      .file("server_app_sync.conf")

  lazy val config =
    configFile
      .readAsStringOpt()
      .map(json.unsafeRead[SyncServer.Config])
      .getOrError(s"config file ${configFile} not found")

  override def runT: ZIO[BootstrapEnv, Throwable, Unit] = {
    val syncServer = SyncServer(config)
    syncServer.run
  }

}
