package io.accur8.neodeploy

import a8.shared.SharedImports._
import a8.shared.FileSystem.{Directory, File}
import a8.shared.ZString
import io.accur8.neodeploy.model.CaddyDirectory
import zio.{Task, ZIO}



case class CaddySync(caddyDir: CaddyDirectory) extends ConfigFileSync(caddyDir) {

  override def filename(deployState: DeployState): ZString =
    z"${deployState.applicationName}.caddy"

  override def state(applicationDescriptor: model.ApplicationDescriptor): Task[Option[String]] =
    zsucceed(
      caddyConfigContents(applicationDescriptor)
    )

  def caddyConfigContents(applicationDescriptor: model.ApplicationDescriptor): Option[String] = {
    import applicationDescriptor._
    val result =
      for {
        listenPort <- applicationDescriptor.listenPort.toIterable
        caddyConfig <- applicationDescriptor.caddyConfig
      } yield
        z"""
${caddyConfig.domainName} {
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

}