package a8.versions


import a8.common.CommonOps._
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import a8.common.Lenser.{Lens, LensImpl}
import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import a8.common.CommonOps._
import a8.common.CompanionGen
import m3.Chord
import Chord._

object model {

  object impl {
    /** quote string */
    def q(s: String): Chord = {
      '"' + s + '"'
    }
  }

  import Json._


  object Repo extends MxRepo

  @CompanionGen
  case class Repo(
    organization: String,
    modules: Iterable[Module]
  )


  object Module extends MxModule

  @CompanionGen
  case class Module(
    sbtName: String,
    artifactName: Option[String],
    directory: Option[String],
    dependsOn: Iterable[String] = Nil,
    dependenciesStr: Option[String],
    dependencies: Iterable[Dependency] = Nil
  ) {
    def resolveArtifactName = artifactName.getOrElse(sbtName)
    def resolveDirectory: String = directory.getOrElse(sbtName)
    def resolveDependencies: Iterable[Dependency] = {
      val d1 =
        dependenciesStr
          .toList
          .flatMap(d => SbtDependencyParser.parse(d))
      dependencies ++ d1
    }
  }


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
    import impl._

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
    override def resolve(versions: Map[String,String]) = impl.q(versions(name))
  }

  case class StringIdentifier(value: String) extends Identifier {
    override def rawValue: String = value
    override def resolve(versions: Map[String,String]) = impl.q(value)
  }


}
