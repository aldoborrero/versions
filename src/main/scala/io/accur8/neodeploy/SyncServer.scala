package io.accur8.neodeploy


import a8.shared.CompanionGen
import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.model.{ApplicationDescriptor, ApplicationName, AppsRootDirectory, CaddyDirectory, Command, DomainName, GitServerDirectory, Install, SupervisorDirectory, UserDescriptor, UserLogin}
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.shared.json.ast
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import a8.versions.Exec
import io.accur8.neodeploy.resolvedmodel.{ResolvedApp, ResolvedServer, ResolvedUser, StoredSyncState}

object SyncServer extends Logging {
}


case class SyncServer(resolvedServer: ResolvedServer) extends LoggingF {

  lazy val stateDirectory: Directory =
    resolvedServer
      .appsRootDirectory
      .unresolvedDirectory
      .subdir(".state")

  lazy val userStateDirectory: Directory =
    stateDirectory
      .subdir("users")
      .resolve

  lazy val applicationStateDirectory: Directory =
    stateDirectory
      .subdir("apps")
      .resolve

  case object userSync extends SyncContainer[ResolvedUser, UserDescriptor, UserLogin]("user", this, userStateDirectory) {

    override val newResolveds: Iterable[ResolvedUser] = resolvedServer.resolvedUsers

    override def descriptorFromResolved(resolved: ResolvedUser): UserDescriptor =
      resolved.descriptor

    override def nameFromStr(value: String): UserLogin =
      UserLogin(value)

    override def name(descriptor: UserDescriptor): UserLogin =
      descriptor.login

    override val syncs: Seq[Sync[_, ResolvedUser]] =
      Seq(AuthorizedKeys2Sync)
  }

  case object appSync extends SyncContainer[ResolvedApp, ApplicationDescriptor, ApplicationName]("app", this, applicationStateDirectory) {

    override val newResolveds: Iterable[ResolvedApp] = resolvedServer.resolvedApps

    override def descriptorFromResolved(resolved: ResolvedApp): ApplicationDescriptor =
      resolved.descriptor

    override def nameFromStr(value: String): ApplicationName =
      ApplicationName(value)

    override def name(descriptor: ApplicationDescriptor): ApplicationName =
      descriptor.name

    override val syncs: Seq[Sync[_, ResolvedApp]] =
      Vector(
        CaddySync(resolvedServer.caddyDirectory),
        SupervisorSync(resolvedServer.supervisorDirectory),
        ApplicationInstallSync(resolvedServer.appsRootDirectory),
      )

    override def runBeforeSync(newResolvedOpt: Option[ResolvedApp], currentStateOpt: Option[ApplicationDescriptor]): Task[Unit] =
      resolvedServer.runAppCommand("stop", currentStateOpt)

    override def runAfterSync(newResolvedOpt: Option[ResolvedApp], currentDescriptorOpt: Option[ApplicationDescriptor]): Task[Unit] =
      resolvedServer.runAppCommand("start", newResolvedOpt.map(_.descriptor))

  }

  def run: Task[Unit] =
    Vector(userSync.run, appSync.run)
      .sequencePar
      .logVoid

}