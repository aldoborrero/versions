package io.accur8.neodeploy


import a8.shared.FileSystem.{Directory, File}
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import a8.shared.ZString
import io.accur8.neodeploy.model.{SupervisorDirectory}
import io.accur8.neodeploy.resolvedmodel.ResolvedApp

case class SupervisorSync(supervisorDir: SupervisorDirectory) extends ConfigFileSync[ResolvedApp] {

  override val name: Sync.SyncName = Sync.SyncName("supervisor")

  override def configFile(input: ResolvedApp): File =
    supervisorDir.unresolvedDirectory.file(z"${input.descriptor.name}.conf")

  override def configFileContents(input: ResolvedApp): Task[Option[String]] =
    zsucceed(
      supervisorConfigContents(input.descriptor).some
    )

  def supervisorConfigContents(applicationDescriptor: model.ApplicationDescriptor) = {
    import applicationDescriptor._
    val resolvedAutoStart = autoStart.getOrElse(true)
    val commandArgs: Seq[String] =
      Seq[ZString](
      z"/opt/bin/${applicationDescriptor.name}",
        "-cp",
        z"'lib/*'",
        z"-Dlog.dir=/opt/logs -Djava.io.tmpdir=/opt/${applicationDescriptor.name}/tmp/",
      ).map(_.toString()) ++
        jvmArgs ++
        Seq[ZString](
          z"-Dapp.name=${applicationDescriptor.name}",
          z"${mainClass}",
        ).map(_.toString()) ++
        appArgs
    z"""
[program:${applicationDescriptor.name}]

command = ${commandArgs.mkString(" ")}

directory = /opt/${applicationDescriptor.name}

autostart       = ${resolvedAutoStart}
autorestart     = ${resolvedAutoStart}
startretries    = 3
startsecs       = 30
redirect_stderr = true
user            = ${user}
""".trim
  }

}
