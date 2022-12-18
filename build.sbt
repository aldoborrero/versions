
// 
// DO NOT EDIT THIS FILE IT IS MACHINE GENERATED
// 
// This file is generated from modules.conf using `a8-versions build_dot_sbt`
// 
// It was generated at 2022-11-19T06:03:37.783520 by glen on stella.local
// 
// a8-versions build/versioning info follows
// 
//        build_date : Thu Nov 17 17:09:32 EST 2022
//        build_machine : stella.hsd1.nh.comcast.net
//        build_user : glen
// 
//      

val appVersion = a8.sbt_a8.versionStamp(file("."))

val scalaLibVersion = "2.13.6"

scalacOptions in Global ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers in Global += "a8-repo" at Common.readRepoUrl()
publishTo in Global := Some("a8-repo-releases" at Common.readRepoUrl())
credentials in Global += Common.readRepoCredentials()

//publishTo in Global := sonatypePublishToBundle.value
//credentials in Global += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")


scalaVersion in Global := scalaLibVersion

organization in Global := "io.accur8"

version in Global := appVersion

versionScheme in Global := Some("strict")

serverConnectionType in Global := ConnectionType.Local


lazy val versions =
  Common
    .jvmProject("a8-versions", file("."), "versions")
    .settings(
      libraryDependencies ++= Seq(
        "ant" % "ant" % "1.6.2",
        "io.get-coursier" %% "coursier" % "2.0.0-RC6",
        "io.get-coursier" %% "coursier-cache" % "2.0.0-RC6",
        "com.softwaremill.sttp" %% "core" % "1.7.2",
        "org.scalameta" %% "fastparse" % "1.0.1",
        "io.accur8" %% "a8-sync-api" % "1.0.0-20221218_1404_master",
        "org.rogach" %% "scallop" % "4.1.0",
        "dev.zio" %% "zio-process" % "0.7.1",
        "org.typelevel" %% "cats-parse" % "0.3.8",
        "org.scalatest" %% "scalatest" % "3.2.12" % "test",
      )
    )


   
