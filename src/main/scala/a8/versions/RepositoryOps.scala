package a8.versions

import java.io.{FileInputStream, StringReader}
import java.util.Properties

import a8.versions.Build.BuildType
import com.softwaremill.sttp.{HttpURLConnectionBackend, Uri, sttp}
import coursier.{Cache, Dependency, Fetch, FileError, Module, Resolution}
import coursier.core.{Authentication, Module}
import coursier.maven.MavenRepository
import m3.fs.dir
import predef._

object RepositoryOps {

  case class DependencyTree(
    resolution: Resolution,
  ) {

    import java.io.File
    import scalaz.\/
    import scalaz.concurrent.Task

    lazy val rawLocalArtifacts: Seq[FileError \/ File] =
      Task.gatherUnordered(
        resolution.artifacts.map(Cache.file(_).run)
      ).unsafePerformSync

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
       Set(
         Dependency(
           module, resolvedVersion.toString
         )
       )
     )

    val repositories =
      if ( buildType.useLocalRepo ) Seq(localRepository, remoteRepository)
      else Seq(remoteRepository)

    val fetch = Fetch.from(repositories, Cache.fetch())

    import scala.concurrent.ExecutionContext.Implicits.global

    val resolution: Resolution = start.process.run(fetch).unsafePerformSync

    val errors: Seq[((Module, String), Seq[String])] = resolution.metadataErrors

    if ( errors.nonEmpty ) {
      throw new RuntimeException(errors.map(_._2.mkString("\n")).mkString("\n"))
    } else {
      DependencyTree(resolution)
    }
  }


  lazy val localRepository = Cache.ivy2Local

  lazy val remoteRepository = {
    val props = new Properties
    props.load(new StringReader(userHome \ ".sbt/credentials" readText))
    val user = props.getProperty("user")
    val password = props.getProperty("password")
    MavenRepository(
      "https://accur8.artifactoryonline.com/accur8/all/",
      authentication = Some(Authentication(user, password))
    )
  }

  def localVersions(module: Module): Iterable[Version] = {

    val moduleDir = ivyLocal.subdir(module.organization).subdir(module.name)

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

    val versionsArtifact = remoteRepository.versionsArtifact(module)

    val uri = Uri(new java.net.URI(versionsArtifact.get.url))

    val response =
      sttp
        .get(uri)
        .auth.basic(remoteRepository.authentication.get.user, remoteRepository.authentication.get.password)
        .send()


    import scala.xml._

    val doc = XML.loadString(response.unsafeBody)

    val versions = doc \\ "version"

    versions
      .map(_.text)
      .flatMap(v => Version.parse(v).toOption)
      .toIndexedSeq
      .sorted(Version.orderingByMajorMinorPathBuildTimestamp)
      .reverse

  }


}
