package a8.appinstaller


import m3.fs._

import language.postfixOps
import m3.predef._

object AppInstaller {
  val standardAppDirectores = Set(
    "cache",
    ".cache",
    "config",
    ".config",
    "data",
    ".data",
    "logs",
    ".logs",
    "temp",
    ".temp",
    "tmp",
    ".tmp"
  )
  val excludeFromConfigDirBackup = List("cache", "cache.dir")
}


case class AppInstaller(config: AppInstallerConfig) extends Logging {

  lazy val installBuilder = InstallBuilder(config)

  lazy val backupDir: LocalFileSystem.TDirectory = config.resolvedAppDir \\ "_bak" \\ net.model3.newfile.Path.getFileSystemCompatibleTimestamp

  def execute(): Unit = {

    stopServer()

    backup()

    backupConfigFiles()

    installBuilder.build()

    if ( config.webappExplode )
      WebappExploderAssist(config.resolvedAppDir)

    startServer()

  }

  def backup(): Unit = tryLog(s"backing up app install directory - ${config.resolvedAppDir.canonicalPath}") {
      backupDir.makeDirectories()
      config
        .resolvedAppDir
        .entries
        .filter(e => !AppInstaller.standardAppDirectores.contains(e.name))
        .foreach { p =>
          p.moveTo(backupDir)
        }
  }

  def backupConfigFiles(): Unit = tryLog(s"backing up config files - ${config.resolvedAppDir.canonicalPath}") {

    List("config", ".config")
      .map(cd => (config.resolvedAppDir \\ cd))
      .filter(_.exists)
      .foreach { cd =>

        logger.debug(s"backing up ${cd.canonicalPath} except for: ${AppInstaller.excludeFromConfigDirBackup.map(d => s"${cd.name}/${d}/").mkString(", ")}")

        val backupConfigDir = (backupDir \\ cd.name)
        backupConfigDir.makeDirectory()

        cd
          .files
          .foreach { file =>
            file.copyTo(backupConfigDir)
          }

        cd
          .subdirs
          .filter(f => !AppInstaller.excludeFromConfigDirBackup.contains(f.name))
          .foreach { dir =>
            val bd = (backupConfigDir \\ dir.name)
            bd.makeDirectory()
            dir.copyTo(bd)
          }
      }
  }

  def stopServer() =
    runCommand("stop server command", config.stopServerCommand)

  def startServer() =
    runCommand("start server command", config.startServerCommand)

  def runCommand(context: String, command: Option[String]) = {
    command.foreach { cmd =>
      logger.info(s"running ${context} command -- ${cmd}")
      import sys.process._
      cmd!
    }
  }

}
