package a8.versions


import a8.common.Lenser.{Lens, LensImpl}
import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import a8.common.CommonOps._


/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import JsonFormats._
import model.Module
import model.Repo
import model.Dependency
import model.Identifier
//====


trait MxRepo {

  implicit val jsonReads: Reads[Repo] = (
    (JsPath \ "organization").read[String] and
    (JsPath \ "modules").read[Iterable[Module]]
  )(Repo.apply _)
  
  implicit val jsonWrites: Writes[Repo] = (
    (JsPath \ "organization").write[String] and
    (JsPath \ "modules").write[Iterable[Module]]
  )(unlift(Repo.unapply))
  
  object lenses {
    val organization: Lens[Repo,String] = LensImpl[Repo,String]("organization", _.organization, (d,v) => d.copy(organization = v))
    val modules: Lens[Repo,Iterable[Module]] = LensImpl[Repo,Iterable[Module]]("modules", _.modules, (d,v) => d.copy(modules = v))
  }
  
  val allLenses = List(lenses.organization,lenses.modules)
  
  val allLensesHList = lenses.organization :: lenses.modules :: shapeless.HNil

}
    



trait MxModule {

  implicit val jsonReads: Reads[Module] = (
    (JsPath \ "sbtName").read[String] and
    (JsPath \ "artifactName").readNullable[String] and
    (JsPath \ "directory").readNullable[String] and
    (JsPath \ "dependsOn").xreadWithDefault[Iterable[String]](Nil) and
    (JsPath \ "dependenciesStr").readNullable[String] and
    (JsPath \ "dependencies").xreadWithDefault[Iterable[Dependency]](Nil
      )
  )(Module.apply _)
  
  implicit val jsonWrites: Writes[Module] = (
    (JsPath \ "sbtName").write[String] and
    (JsPath \ "artifactName").writeNullable[String] and
    (JsPath \ "directory").writeNullable[String] and
    (JsPath \ "dependsOn").write[Iterable[String]] and
    (JsPath \ "dependenciesStr").writeNullable[String] and
    (JsPath \ "dependencies").write[Iterable[Dependency]]
  )(unlift(Module.unapply))
  
  object lenses {
    val sbtName: Lens[Module,String] = LensImpl[Module,String]("sbtName", _.sbtName, (d,v) => d.copy(sbtName = v))
    val artifactName: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("artifactName", _.artifactName, (d,v) => d.copy(artifactName = v))
    val directory: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("directory", _.directory, (d,v) => d.copy(directory = v))
    val dependsOn: Lens[Module,Iterable[String]] = LensImpl[Module,Iterable[String]]("dependsOn", _.dependsOn, (d,v) => d.copy(dependsOn = v))
    val dependenciesStr: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("dependenciesStr", _.dependenciesStr, (d,v) => d.copy(dependenciesStr = v))
    val dependencies: Lens[Module,Iterable[Dependency]] = LensImpl[Module,Iterable[Dependency]]("dependencies", _.dependencies, (d,v) => d.copy(dependencies = v))
  }
  
  val allLenses = List(lenses.sbtName,lenses.artifactName,lenses.directory,lenses.dependsOn,lenses.dependenciesStr,lenses.dependencies)
  
  val allLensesHList = lenses.sbtName :: lenses.artifactName :: lenses.directory :: lenses.dependsOn :: lenses.dependenciesStr :: lenses.dependencies :: shapeless.HNil

}
    



trait MxDependency {

  implicit val jsonReads: Reads[Dependency] = (
    (JsPath \ "organization").read[String] and
    (JsPath \ "scalaArtifactSeparator").read[String] and
    (JsPath \ "artifactName").read[String] and
    (JsPath \ "version").read[Identifier] and
    (JsPath \ "configuration").readNullable[String] and
    (JsPath \ "exclusions").read[Iterable[(String, String)]]
  )(Dependency.apply _)
  
  implicit val jsonWrites: Writes[Dependency] = (
    (JsPath \ "organization").write[String] and
    (JsPath \ "scalaArtifactSeparator").write[String] and
    (JsPath \ "artifactName").write[String] and
    (JsPath \ "version").write[Identifier] and
    (JsPath \ "configuration").writeNullable[String] and
    (JsPath \ "exclusions").write[Iterable[(String, String)]]
  )(unlift(Dependency.unapply))
  
  object lenses {
    val organization: Lens[Dependency,String] = LensImpl[Dependency,String]("organization", _.organization, (d,v) => d.copy(organization = v))
    val scalaArtifactSeparator: Lens[Dependency,String] = LensImpl[Dependency,String]("scalaArtifactSeparator", _.scalaArtifactSeparator, (d,v) => d.copy(scalaArtifactSeparator = v))
    val artifactName: Lens[Dependency,String] = LensImpl[Dependency,String]("artifactName", _.artifactName, (d,v) => d.copy(artifactName = v))
    val version: Lens[Dependency,Identifier] = LensImpl[Dependency,Identifier]("version", _.version, (d,v) => d.copy(version = v))
    val configuration: Lens[Dependency,Option[String]] = LensImpl[Dependency,Option[String]]("configuration", _.configuration, (d,v) => d.copy(configuration = v))
    val exclusions: Lens[Dependency,Iterable[(String, String)]] = LensImpl[Dependency,Iterable[(String, String)]]("exclusions", _.exclusions, (d,v) => d.copy(exclusions = v))
  }
  
  val allLenses = List(lenses.organization,lenses.scalaArtifactSeparator,lenses.artifactName,lenses.version,lenses.configuration,lenses.exclusions)
  
  val allLensesHList = lenses.organization :: lenses.scalaArtifactSeparator :: lenses.artifactName :: lenses.version :: lenses.configuration :: lenses.exclusions :: shapeless.HNil

}
    