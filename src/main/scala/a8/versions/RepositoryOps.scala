package a8.versions

import a8.shared.jdbcf.DatabaseConfig.Password
import a8.shared.{CompanionGen, FileSystem, StringValue}

import java.io.{FileInputStream, StringReader}
import java.util.Properties
import a8.versions.Build.BuildType
import a8.versions.RepositoryOps.{DependencyTree, RepoConfigPrefix, ivyLocal}
import a8.versions.Upgrade.LatestArtifact
import a8.versions.model.{ArtifactResponse, ResolutionRequest, ResolutionResponse}
import a8.versions.predef._
import com.softwaremill.sttp.{Request, Uri, sttp}
import coursier.cache.{ArtifactError, Cache}
import coursier.core.{Authentication, Module, ResolutionProcess}
import coursier.maven.MavenRepository
import coursier.util.{Artifact, EitherT, Task}
import coursier.{Dependency, LocalRepositories, Profile, Resolution}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object RepositoryOps extends Logging {

  val default = RepositoryOps(RepoConfigPrefix.default)

  object RepoConfigPrefix extends StringValue.Companion[RepoConfigPrefix] {
    def default = RepoConfigPrefix("repo")
  }

  case class RepoConfigPrefix(
    value: String,
  ) extends StringValue {

    object impl {
      def readRepoPropertyOpt(suffix: String): Option[String] = {
        val propertyName = s"${value}_${suffix}"
        val result =
          RepoAssist.readRepoPropertyOpt(propertyName) match {
            case None =>
              None
            case Some(s) =>
              Some(s)
          }
        logger.debug("reading repo property: " + propertyName + " = " + result)
        result
      }
    }

    lazy val url =
      (value, impl.readRepoPropertyOpt("url")) match {
        case (_, Some(v)) =>
          v
        case ("maven", _) =>
          val v = "https://repo1.maven.org/maven2"
          logger.debug("using default maven repo url: " + v)
          v
        case _ =>
          sys.error(s"minimally must supply a ${value}_url property or specify the repo as maven")
      }

    lazy val userOpt = impl.readRepoPropertyOpt("user")
    lazy val passwordOpt =  impl.readRepoPropertyOpt("password")

    def authentication: Option[Authentication] = {
      for {
        user <- userOpt
        password <- passwordOpt
      } yield Authentication(user, password)
    }
  }

  case class DependencyTree(
    resolution: Resolution,
  ) {

    import java.io.File

    lazy val rawLocalArtifacts: Seq[Either[ArtifactError, File]] =
        resolution.artifacts().map(Cache.default.file(_).run.unsafeRun())

    lazy val localArtifacts: Seq[File] =
      rawLocalArtifacts
        .flatMap(_.toOption)
        .filter{ f =>
          f.getName.endsWith(".jar")
        }
        .distinct

  }

  lazy val userHome = FileSystem.dir(System.getProperty("user.home"))

  lazy val ivyLocal = userHome \\ ".ivy2" \\ "local"

  def runResolve(request: ResolutionRequest): ResolutionResponse = {
    val repositoryOps = RepositoryOps(request.repoPrefix)

    implicit val buildType = BuildType.ArtifactoryBuild

    lazy val resolvedVersion: Version =
      request.version match {
        case "latest" =>
          LatestArtifact(request.coursierModule, request.branch.getOrElse(sys.error("branch is required for latest")))
            .resolveVersion(Map.empty, repositoryOps)
        case s =>
          Version
            .parse(s)
            .getOrElse(sys.error(s"Invalid version: $s"))
      }

    lazy val dependencyTree: RepositoryOps.DependencyTree =
      repositoryOps
        .resolveDependencyTree(request.coursierModule, resolvedVersion)

    lazy val resolution: Resolution = dependencyTree.resolution

    lazy val artifactResponses: Seq[ArtifactResponse] =
      resolution
        .dependencyArtifacts(None)
        .collect {
          case t@ (dep, pub, artifact) if Resolution.defaultTypes(pub.`type`) =>
            t
        }
        .distinctBy(_._3)
        .map { case (dep, pub, artifact) =>
          ArtifactResponse(
            artifact.url,
            dep.module.organization.value,
            dep.module.name.value,
            dep.version,
            pub.ext.value,
          )
        }

    lazy val response =
      ResolutionResponse(
        request,
        resolvedVersion.toString(),
        artifactResponses,
      )

    response

  }

}

case class RepositoryOps(repoConfigPrefix: RepoConfigPrefix) extends Logging {

  def resolveDependencyTree(module: Module, resolvedVersion: Version)(implicit buildType: BuildType): DependencyTree = {

    // a8-qubes-server_2.12/2.7.0-20180324_1028_master

    val start = Resolution(
       Seq(
         Dependency(
           module, resolvedVersion.toString()
         )
       )
     )

    val repositories =
      if ( buildType.useLocalRepo ) Seq(localRepository, remoteRepository)
      else Seq(remoteRepository)

    val fetch = ResolutionProcess.fetch(repositories, Cache.default.fetch)

    val resolution: Resolution = start.process.run(fetch).unsafeRun()

    val errors: Seq[((Module, String), Seq[String])] = resolution.errors

    if ( errors.nonEmpty ) {
      throw new RuntimeException(errors.map(_._2.mkString("\n")).mkString("\n"))
    } else {
      DependencyTree(resolution)
    }
  }


  lazy val localRepository = LocalRepositories.ivy2Local

  def remoteRepositoryUri = repoConfigPrefix.url
//  def remoteRepositoryUser = repoConfig.user
//  def remoteRepositoryPassword = repoConfig.password

  lazy val remoteRepository =
    MavenRepository(
      remoteRepositoryUri.toString,
      authentication = remoteRepositoryAuthentication,
    )

  def remoteRepositoryAuthentication: Option[Authentication] = repoConfigPrefix.authentication

  def localVersions(module: Module): Iterable[Version] = {

    val moduleDir = ivyLocal.subdir(module.organization.value).subdir(module.name.value)

    moduleDir
      .subdirs()
      .flatMap { d =>
        Version.parse(d.name).toOption
      }

  }

  /**
    * sorted with most recent version first
    */
  def remoteVersions(module: Module): Iterable[Version] = {

    def getVersionXml(artifact: Artifact): Future[Either[String,String]] = {
      try {
        val uri = Uri(new java.net.URI(artifact.url)).copy(userInfo = None)

        def addAuth(request: Request[String, Nothing]): Request[String,Nothing] = {
          remoteRepositoryAuthentication
            .map(auth => request.auth.basic(auth.user, auth.passwordOpt.get))
            .getOrElse(request)
        }

        val response = { //url.openStream().readString
          addAuth(sttp.get(uri))
            .send()
        }

        val body = response.unsafeBody

        logger.debug("================ " + artifact.url + "\n" + body)

        Future.successful(Right(body))
      } catch {
        case e: Throwable =>
          Future.failed(e)
      }
    }

    def fetch(artifact: Artifact): EitherT[Task, String, String] = EitherT(Task(_ => getVersionXml(artifact)))

    val versions =
      remoteRepository.versions(module, fetch).run.unsafeRun() match {
        case Right((value, _)) =>
          value.available
        case Left(msg) =>
          sys.error(msg)
      }

    versions
      .flatMap(v => Version.parse(v).toOption)
      .toIndexedSeq
      .sorted(Version.orderingByMajorMinorPathBuildTimestamp)
      .reverse

  }

}
