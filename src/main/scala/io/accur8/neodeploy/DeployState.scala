package io.accur8.neodeploy


import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.model.{ApplicationDescriptor, JavaVersion}
import zio.{Task, ZIO}
import a8.shared.SharedImports._

case class DeployState(
  applicationName: model.ApplicationName,
  gitAppDirectory: Directory,
  currentApplicationDescriptor: Option[ApplicationDescriptor],
  newApplicationDescriptor: Option[ApplicationDescriptor],
) {

  def javaVersion: JavaVersion = newApplicationDescriptor.flatMap(_.javaVersion).get

  def stopCommand =
    currentApplicationDescriptor
      .toSeq
      .flatMap(_.resolvedStopCommand)

  def startCommand =
    newApplicationDescriptor
      .toSeq
      .flatMap(_.resolvedStartCommand)

}
