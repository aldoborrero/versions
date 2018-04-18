package example

import a8.versions.Build.BuildType
import a8.versions.{RepositoryOps, Version}

object DependencyTreeDemo {


  def main(args: Array[String]): Unit = {
    implicit val buildType = BuildType.ArtifactoryBuild

    val tree =
      RepositoryOps
        .resolveDependencyTree(
          coursier.core.Module("a8", "a8-qubes-dist_2.12", Map()),
          Version.parse("2.7.0-20180418_0536_master").get
        )

    val searchStr = "scala-compiler"

    val artifacts = tree.resolution.artifacts.filter(_.url.contains(searchStr))

    val errors = tree.resolution.metadataErrors

    val files = tree.localArtifacts.sortBy(_.getCanonicalPath)

    val lefts = tree.rawLocalArtifacts.filter(_.isLeft)
    val rights = tree.rawLocalArtifacts.filter(_.isRight)

    val scalaps = files.filter(_.getCanonicalPath.contains(searchStr))

    toString

  }

}
