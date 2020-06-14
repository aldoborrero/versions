package a8.versions

import java.io.{FileInputStream, StringReader}
import java.util.Properties

import a8.versions.Build.BuildType
import a8.versions.predef._
import com.softwaremill.sttp.{Uri, sttp}
import coursier.cache.{ArtifactError, Cache}
import coursier.core.{Authentication, Module, ResolutionProcess}
import coursier.maven.MavenRepository
import coursier.util.{Artifact, EitherT, Task}
import coursier.{Dependency, LocalRepositories, Resolution}
import m3.fs.dir

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

object RepositoryOps {

  case class DependencyTree(
    resolution: Resolution,
  ) {

    import java.io.File

    lazy val rawLocalArtifacts: Seq[Either[ArtifactError, File]] =
        resolution.artifacts.map(Cache.default.file(_).run.unsafeRun())

    lazy val localArtifacts: Seq[File] =
      rawLocalArtifacts
        .flatMap(_.toOption)
        .filter{ f =>
          f.getName.endsWith(".jar")
        }

  }

  lazy val userHome = dir(System.getProperty("user.home"))

  lazy val ivyLocal = userHome \\ ".ivy2" \\ "local"

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

  lazy val remoteRepositoryUri = Uri(new java.net.URI(RepoAssist.readRepoProperty("default_repo_url")))
  lazy val remoteRepositoryUserInfo = remoteRepositoryUri.userInfo.getOrError(s"no user:password found in default_repo_url ${remoteRepositoryUri}")
  lazy val remoteRepositoryUser = remoteRepositoryUserInfo.username
  lazy val remoteRepositoryPassword = remoteRepositoryUserInfo.password.getOrError(s"no password found in default_repo_url ${remoteRepositoryUri}")

  lazy val remoteRepository =
    MavenRepository(
      remoteRepositoryUri.toString,
      authentication = Some(Authentication(remoteRepositoryUser, remoteRepositoryPassword))
    )

  def localVersions(module: Module): Iterable[Version] = {

    val moduleDir = ivyLocal.subdir(module.organization.value).subdir(module.name.value)

    moduleDir
      .subdirs
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

        val response = //url.openStream().readString
          sttp
            .get(uri)
            .auth.basic(remoteRepositoryUser, remoteRepositoryPassword)
            .send()

        val body = response.unsafeBody

        println("================ " + artifact.url)
        println(body)

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
