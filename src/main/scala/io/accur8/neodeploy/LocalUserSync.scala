package io.accur8.neodeploy


import a8.shared.{CompanionGen, StringValue}
import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.model.{ApplicationDescriptor, ApplicationName, AppsRootDirectory, CaddyDirectory, DomainName, GitServerDirectory, Install, SupervisorDirectory, UserDescriptor, UserLogin}
import zio.{Task, UIO, ZIO}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.shared.json.ast
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import io.accur8.neodeploy.Sync.Phase
import io.accur8.neodeploy.resolvedmodel.{ResolvedApp, ResolvedRSnapshotServer, ResolvedServer, ResolvedUser, StoredSyncState}



case class LocalUserSync(resolvedUser: ResolvedUser, filterApps: Vector[ApplicationName]) extends LoggingF {

  lazy val resolvedServer = resolvedUser.server

  lazy val stateDirectory: Directory =
    resolvedUser
      .home
      .subdir(".neodeploy-state")
      .resolve

  lazy val healthchecksDotIo = HealthchecksDotIo(resolvedServer.repository.descriptor.healthchecksApiToken)

  case object userSync extends SyncContainer[ResolvedUser, UserDescriptor, UserLogin](SyncContainer.Prefix("user"), this, stateDirectory) {

    override val newResolveds: Iterable[ResolvedUser] = Iterable(resolvedUser)

    override def descriptorFromResolved(resolved: ResolvedUser): UserDescriptor =
      resolved.descriptor

    override def nameFromStr(value: String): UserLogin =
      UserLogin(value)

    override def name(descriptor: UserDescriptor): UserLogin =
      descriptor.login

    override val syncs: Seq[Sync[_, ResolvedUser]] =
      Seq(
        AuthorizedKeys2Sync,
        new ManagedSshKeysSync,
        PGBackrestSync(resolvedServer.repository.descriptor.healthchecksApiToken),
        RSnapshotServerSync(healthchecksDotIo),
      )

  }

  case object appSync extends SyncContainer[ResolvedApp, ApplicationDescriptor, ApplicationName](SyncContainer.Prefix("app"), this, stateDirectory, filterApps) {

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
        ApplicationInstallSync(resolvedUser.appsRootDirectory),
      )

    override def additionalSteps(name: ApplicationName, newResolvedOpt: Option[ResolvedApp], currentStateOpt: Option[ApplicationDescriptor], containerSteps: Sync.ContainerSteps): Seq[Sync.Step] = {

      val stopAppSteps =
        if ( containerSteps.nonEmpty)
          resolvedServer.appCommandStep(Phase.Pre, "stop", _.stopServerCommand, currentStateOpt)
        else
          Seq.empty

      val startAppSteps =
        if (containerSteps.nonEmpty)
          resolvedServer.appCommandStep(Phase.Post, "start", _.startServerCommand, newResolvedOpt.map(_.descriptor))
        else
          Seq.empty

      stopAppSteps ++ startAppSteps

    }

  }

  def run: UIO[Vector[(StringValue, Either[Throwable, Unit])]] = {
    loggerF.info(z"running for ${resolvedUser.qualifiedUserName}") *>
    // we only sync 1 user
    userSync
      .run
      .zipPar(appSync.run)
      .map { case (u, a) =>
        u ++ a
      }
  }

}