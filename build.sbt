



scalacOptions in Global ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers in Global += "a8-repo" at "https://accur8.jfrog.io/accur8/all/"

publishTo in Global := Some("a8-repo-publish" at "https://accur8.jfrog.io/accur8/libs-releases-local/")

credentials in Global += Credentials(Path.userHome / ".sbt" / "credentials")

scalaVersion in Global := "2.12.6"

organization in Global := "a8"

version in Global := a8.sbt_a8.versionStamp(file("."))



lazy val versions =
  Common
    .jvmProject("a8-versions", file("."), "versions")
    .settings(
      libraryDependencies ++= Seq(
        "io.get-coursier" %% "coursier" % "1.0.3" % "compile",
        "io.get-coursier" %% "coursier-cache" % "1.0.3" % "compile",
        "com.softwaremill.sttp" %% "core" % "1.1.11" % "compile",
        "org.scala-lang.modules" %% "scala-xml" % "1.1.0" % "compile",
        "com.lihaoyi" %% "fastparse" % "1.0.0" % "compile",
        "a8" %% "a8-common" % "2.7.1-20181108_0912_master" % "compile",
        "a8" %% "m3-impl-api" % "2.7.1-20181018_1211_master" % "compile",
        "com.beachape" %% "enumeratum-play-json" % "1.5.14" % "compile",
        "org.rogach" %% "scallop" % "3.1.2" % "compile",
        "org.typelevel" %% "cats-core" % "1.0.1" % "compile",
      )
    )


   