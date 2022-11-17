package io.accur8.neodeploy

import a8.shared.SharedImports._
import a8.shared.FileSystem.{Directory, File}
import a8.shared.ZString
import a8.shared.app.LoggingF
import io.accur8.neodeploy.model.{CaddyDirectory}
import io.accur8.neodeploy.resolvedmodel.ResolvedApp
import zio.{Task, ZIO}

object CaddySync extends LoggingF {

  val reloadCaddyStep =
    Sync.Step.runCommand(
      phase = Sync.Phase.Post,
      command = Overrides.systemCtlCommand.appendArgs("reload", "caddy"),
    ).some

}

case class CaddySync(caddyDir: CaddyDirectory) extends ConfigFileSync[ResolvedApp] {

  override val name: Sync.SyncName = Sync.SyncName("caddy")

  override def configFile(resolvedApp: ResolvedApp): File =
    caddyDir.unresolvedDirectory.file(z"${resolvedApp.descriptor.name}.caddy")

  override def configFileContents(input: ResolvedApp): Task[Option[String]] =
    zsucceed(caddyConfigContents(input.descriptor))

  def caddyConfigContents(applicationDescriptor: model.ApplicationDescriptor): Option[String] = {
    import applicationDescriptor._
    val result =
      for {
        listenPort <- applicationDescriptor.listenPort.toIterable
        _ <- applicationDescriptor.resolvedDomainNames.nonEmpty.toOption(())
      } yield
        z"""
${applicationDescriptor.resolvedDomainNames.map(_.value).mkString(", ")} {
  encode gzip
  reverse_proxy localhost:${listenPort}
}
""".trim

    result match {
      case r if r.isEmpty =>
        None
      case r =>
        Some(r.mkString("\n\n"))
    }

  }

  override def resolveStepsFromModification(modification: Sync.Modification[ConfigFileSync.State, ResolvedApp]): Vector[Sync.Step] =
    super.resolveStepsFromModification(modification) ++ CaddySync.reloadCaddyStep

}