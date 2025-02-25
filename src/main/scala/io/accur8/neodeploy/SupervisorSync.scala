package io.accur8.neodeploy


import a8.shared.ZFileSystem.{Directory, File}
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import a8.shared.{FileSystem, ZString}
import a8.shared.ZString.ZStringer
import io.accur8.neodeploy.model.{SupervisorDescriptor, SupervisorDirectory}
import io.accur8.neodeploy.resolvedmodel.ResolvedApp
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel.M

case class SupervisorSync(supervisorDir: SupervisorDirectory) extends Sync[ResolvedApp] {

  override val name: Sync.SyncName = Sync.SyncName("supervisor")

  def configFile(input: ResolvedApp): File =
    supervisorDir.file(z"${input.descriptor.name}.conf")

  def supervisorConfigContents(app: ResolvedApp, supervisor: SupervisorDescriptor) = {

    def pathZStringer[A <: a8.shared.ZFileSystem.Path]: ZStringer[A] =
      new ZStringer[A] {
        override def toZString(a: A): ZString =
          a.asNioPath.toAbsolutePath.toString
      }

    implicit val dirZStringer = pathZStringer[Directory]
    implicit val fileZStringer = pathZStringer[File]

    import app.descriptor._

    val resolvedAutoStart = supervisor.autoStart.getOrElse(true)
    val resolvedStartRetries = supervisor.startRetries.getOrElse(0)
    val resolvedAutoRestart = supervisor.autoRestart.getOrElse(true)
    val resolvedStartSecs = supervisor.startSecs.getOrElse(5)

    val appsRoot = app.user.appsRootDirectory
    val bin = appsRoot.subdir("bin").file(app.descriptor.name.value)
    val logsDir = appsRoot.subdir("logs")
    val appDir = appsRoot.subdir(app.descriptor.name.value)
    val tempDir = appDir.subdir("tmp")
    val commandArgs = app.descriptor.install.execArgs(app.descriptor, app.appDirectory, app.user.appsRootDirectory)

    z"""
[program:${app.descriptor.name}]

command = ${commandArgs.mkString(" ")}

directory = ${appDir}

autostart       = ${resolvedAutoStart}
autorestart     = ${resolvedAutoRestart}
startretries    = ${resolvedStartRetries}
startsecs       = ${resolvedStartSecs}
redirect_stderr = true
user            = ${app.user.login}

#
""".trim
  }


  override def systemState(user: ResolvedApp): M[SystemState] =
    zsucceed(rawSystemState(user))

  def rawSystemState(user: ResolvedApp): SystemState =
    user.descriptor.launcher match {
      case sd: SupervisorDescriptor =>
        SystemState.TextFile(
          configFile(user),
          supervisorConfigContents(user, sd)
        )
      case _ =>
        SystemState.Empty
    }

}
