package io.accur8.neodeploy


//import a8.shared.FileSystem.Directory
//import io.accur8.neodeploy.model.{ApplicationDescriptor, Command, JavaVersion, ResolvedApp, StoredApplicationState}
//import zio.{Task, ZIO}
//import a8.shared.SharedImports._
//import a8.shared.json.ast.JsVal
//
//case class DeployState(
//  applicationName: model.ApplicationName,
//  gitAppDirectory: Directory,
//  currentApplicationState: Option[StoredApplicationState],
//  newResolvedApp: Option[ResolvedApp],
//) {
//
//  lazy val currentApplicationDescriptor: Option[ApplicationDescriptor] =
//    currentApplicationState.map(_.applicationDescriptor)
//
//  lazy val newApplicationDescriptor: Option[ApplicationDescriptor] =
//    newResolvedApp.map(_.application)
//
//  def needsSync: Boolean = currentApplicationDescriptor != newApplicationDescriptor
//
//  def javaVersion: JavaVersion = newApplicationDescriptor.flatMap(_.javaVersion).get
//
//  lazy val stopCommand: Option[Command] =
//    currentApplicationDescriptor
//      .map(_.resolvedStopCommand)
//
//  lazy val startCommand: Option[Command] =
//    newApplicationDescriptor
//      .map(_.resolvedStartCommand)
//
//}
