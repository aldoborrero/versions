package example

import a8.versions.SbtDependencyParser

object ParseDependencies extends App {

  val deps = """
        "com.github.ghik" %% "silencer-lib" % "0.5" % "compile",
        "com.lihaoyi" %% "sourcecode" % "0.1.4" % "compile",
        "javax.servlet" % "servlet-api" % "2.5" % "provided",
        "com.palominolabs.metrics" % "metrics-guice" % "3.1.3" % "compile"
          exclude("com.google.code.findbugs", "jsr305")
          exclude("com.google.inject", "guice"),
        "io.dropwizard.metrics" % "metrics-jvm" % "3.1.2" % "compile",
        "io.dropwizard.metrics" % "metrics-core" % "3.1.2" % "compile",
        "io.dropwizard.metrics" % "metrics-annotation" % "3.1.2" % "compile",
        "io.dropwizard.metrics" % "metrics-healthchecks" % "3.1.2" % "compile",
        "com.jcraft" % "jsch" % "0.1.48" % "compile",
        "com.netaporter" %% "scala-uri" % "0.4.16" % "compile",
        "io.dropwizard.metrics" % "metrics-log4j2" % "3.1.2" % "compile"
          exclude("org.apache.logging.log4j", "log4j-api")
          exclude("org.apache.logging.log4j", "log4j-core"),
        "junit" % "junit" % "4.10" % "test",
        "ant" % "ant" % "1.6.2" % "compile",
        "aopalliance" % "aopalliance" % "1.0" % "compile",
        "org.apache.logging.log4j" % "log4j-api" % log4jVersion % "compile",
        "org.scalatest" %% "scalatest" % "3.0.1" % "test",
        "org.apache.logging.log4j" % "log4j-core" % log4jVersion % "compile",
        "org.apache.logging.log4j" % "log4j-1.2-api" % log4jVersion % "compile",
        "org.slf4j" % "jcl-over-slf4j" % "1.7.25",
        "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion % "compile",
        "org.apache.logging.log4j" % "log4j-jul" % log4jVersion % "compile",
        "javax.mail" % "mail" % "1.4.5" % "compile",
        "javax.activation" % "activation" % "1.1.1" % "compile",
        "com.google.inject.extensions" % "guice-grapher" % "4.1.0" % "compile",
        "com.google.inject.extensions" % "guice-multibindings" % "4.1.0" % "compile",
        "com.google.inject.extensions" % "guice-assistedinject" % "4.1.0" % "compile",
        "javax.inject" % "javax.inject" % "1" % "compile",
        "org.scala-lang" % "scalap" % scalaVersion % "compile"
    """

  val parms =
    Map(
      "log4jVersion" -> "2.9.1",
      "scalaVersion" -> "2.12.5"
    )

  SbtDependencyParser.parse(deps)
    .foreach(println)

}
