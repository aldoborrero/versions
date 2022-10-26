package io.accur8.neodeploy


import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.model.{ApplicationDescriptor, Command, JavaVersion}
import zio.{Task, ZIO}
import a8.shared.SharedImports._

case class DeployState(
  applicationName: model.ApplicationName,
  gitAppDirectory: Directory,
  currentApplicationDescriptor: Option[ApplicationDescriptor],
  newApplicationDescriptor: Option[ApplicationDescriptor],
) {

  def needsSync: Boolean = currentApplicationDescriptor != newApplicationDescriptor

  def javaVersion: JavaVersion = newApplicationDescriptor.flatMap(_.javaVersion).get

  lazy val stopCommand: Option[Command] =
    currentApplicationDescriptor
      .map(_.resolvedStopCommand)

  lazy val startCommand: Option[Command] =
    newApplicationDescriptor
      .map(_.resolvedStartCommand)

}
