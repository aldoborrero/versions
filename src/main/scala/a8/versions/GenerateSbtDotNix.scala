package a8.versions

import a8.shared.FileSystem
import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import a8.versions.Build.BuildType
import a8.versions.RepositoryOps.RepoConfigPrefix
import a8.versions.Version.BuildInfo
import a8.versions.model.{ArtifactResponse, BranchName, ResolutionRequest}
import coursier.ModuleName
import coursier.core.Module
import zio.ZIO

import java.io.{File, BufferedWriter, FileWriter}
import java.net.URL
import java.security.MessageDigest
import java.util.Base64

object GenerateSbtDotNix extends App {

  val createLocalM2Repo = System.getProperty("createLocalM2Repo", "true").toBoolean

  val resolutionRequest =
    ResolutionRequest(
      repoPrefix = RepoConfigPrefix("maven"),
      organization = "io.accur8",
      artifact = "a8-sync-api_2.13",
      version = "1.0.0-20221219_0641_master",
      branch = None,
    )


  // hardcoded to use maven for now
  val resolutionResponse = RepositoryOps.runResolve(resolutionRequest)

  println(s"fetching sha256 checksums for ${resolutionResponse.artifacts.size} artifacts")

  val artifacts =
    resolutionResponse
      .artifacts
      .map { artifact =>
        artifact -> nixPrefetchUrl(artifact.url)
      }

  def fetchLine(artifact: ArtifactResponse, nixPrefetchResult: NixPrefetchResult): String = {
//      artifact
//        .checksums
//        .find(_.toLowerCase == "sha-256")
//        .getOrElse(fetchSha256(artifact.url))
    val attributes =
      Vector(
        "url" -> artifact.url,
        "groupId" -> artifact.organization,
        "artifactId" -> artifact.module,
        "version" -> artifact.version,
        "sha256" -> nixPrefetchResult.nixHash,
        // "m2RepoPath" -> artifact.m2RepoPath
      )
    s"""    {name = "${artifact.organization}/${artifact.module}"; maven = {${attributes.map(t => s"""${t._1} = "${t._2}"; """).mkString(" ")}}; }"""
  }

  def writeFile(file: File, s: String): Unit = {
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }


  val sbtNixDeps = 
    s"""
[
${artifacts.map(t => fetchLine(t._1, t._2)).mkString("\n")}
]
""".trim + "\n"

  val sbtDepsfile = new File("sbt-deps.nix").getAbsoluteFile
  writeFile(sbtDepsfile, sbtNixDeps)


  case class NixPrefetchResult(
    nixHash: String,
    nixStorePath: String,
  )

  def nixPrefetchUrl(url: String): NixPrefetchResult = {
    import sys.process._
    import scala.language.postfixOps
    val results = (s"nix-prefetch-url --print-path ${url}" !!)
    val lines =
      results
        .linesIterator
        .filter(_.trim.length > 0)
        .toVector
    val result =
      NixPrefetchResult(
        lines(0),
        lines(1),
      )
    println(s"nix prefetch of ${url} ${result}")
    result
  }


  if ( createLocalM2Repo ) {
    val repoRoot = new File("m2-local-repo").getAbsoluteFile
    artifacts
      .foreach { case (artifact, nixPrefetch) =>
        val repoFile = new File(repoRoot, artifact.m2RepoPath)
        if ( !repoFile.exists() ) {
          import sys.process._
          import scala.language.postfixOps
          if ( !repoFile.getParentFile.exists() )
            repoFile.getParentFile.mkdirs()
          s"ln -s ${nixPrefetch.nixStorePath} ${repoFile}" !
        }
      }
  }

}
