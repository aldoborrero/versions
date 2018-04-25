import Dependencies._

lazy val appVersion = {
  val now = java.time.LocalDateTime.now()
  val timestamp = f"${now.getYear}%02d${1+now.getMonth.ordinal}%02d${now.getDayOfMonth}%02d_${now.getHour}%02d${now.getMinute}%02d"
  val v = s"1.0.0-${timestamp}_master"
  println(s"setting version to ${v}")
  v
}

resolvers in Global += "a8-repo" at "https://accur8.artifactoryonline.com/accur8/all/"

publishTo in Global := Some("a8-repo-publish" at "https://accur8.artifactoryonline.com/accur8/libs-releases-local/")

credentials in Global += Credentials(Path.userHome / ".sbt" / "credentials")


lazy val versions =
  (project in file(".")).
    settings(
      inThisBuild(List(
        organization := "a8",
        scalaVersion := "2.12.5",
        version      := appVersion
      )),
      scalacOptions += "-Ypartial-unification",
      name := "a8-versions",
      libraryDependencies += scalaTest % Test,
      libraryDependencies ++= Seq(
        "io.get-coursier" %% "coursier" % "1.0.3",
        "io.get-coursier" %% "coursier-cache" % "1.0.3",
        "com.softwaremill.sttp" %% "core" % "1.1.11",
        "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
        "com.lihaoyi" %% "fastparse" % "1.0.0",
        "a8" %% "a8-common" % "2.7.1-20180421_1437_master",
        "a8" %% "m3-impl-api" % "2.7.0-20180410_2001_master",
        "com.beachape" %% "enumeratum-play-json" % "1.5.14",
        "org.rogach" %% "scallop" % "3.1.2",
        "org.typelevel" %% "cats-core" % "1.0.1",
      )
    )
    .withId("versions")
