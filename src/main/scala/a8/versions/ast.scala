package a8.versions

import a8.common.CompanionGen
import a8.common.HoconOps._
import m3.Chord
import m3.Chord._

object ast {

  import model.impl.q

  object Repo extends MxRepo

  @CompanionGen
  case class Repo(
    header: Option[String] = None,
    organization: String,
    modules: Iterable[Module],
  )


  object Module extends MxModule

  @CompanionGen
  case class Module(
    sbtName: String,
    projectType: Option[String],
    artifactName: Option[String],
    directory: Option[String],
    dependsOn: Iterable[String] = Nil,
    dependencies: Option[String],
    jvmDependencies: Option[String],
    jsDependencies: Option[String],
    extraSettings: Option[String],
  )

  object Dependency extends MxDependency

  @CompanionGen
  case class Dependency(
    organization: String,
    scalaArtifactSeparator: String,
    artifactName: String,
    version: Identifier,
    configuration: Option[String],
    exclusions: Iterable[(String, String)],
  ) {

    def exclusionsAsSbt =
      exclusions
        .map { exclusion =>
          "exclude(" ~ q(exclusion._1) ~ "," * q(exclusion._2) ~ ")"
        }
        .mkChord(" ")

    val configurationAsSbt =
      configuration.map(c => "%" * q(c))

    def asSbt(versionDotPropsMap: Map[String,String]) = {
      q(organization) * scalaArtifactSeparator * q(artifactName) * "%" * version.resolve(versionDotPropsMap) * configurationAsSbt * exclusionsAsSbt
    }
  }


  sealed trait Identifier {
    def rawValue: String
    def resolve(versions: Map[String,String]): Chord
  }

  case class VariableIdentifier(name: String) extends Identifier {
    override def rawValue: String = name
    override def resolve(versions: Map[String,String]) = q(versions(name))
  }

  case class StringIdentifier(value: String) extends Identifier {
    override def rawValue: String = value
    override def resolve(versions: Map[String,String]) = q(value)
  }


}
