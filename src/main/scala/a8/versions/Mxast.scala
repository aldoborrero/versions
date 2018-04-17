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
import ast._
//====


trait MxRepo {

  implicit val jsonReads: Reads[Repo] = (
    (JsPath \ "header").xreadNullableWithDefault[String](None) and
    (JsPath \ "organization").read[String] and
    (JsPath \ "modules").read[Iterable[Module]]
  )(Repo.apply _)
  
  implicit val jsonWrites: Writes[Repo] = (
    (JsPath \ "header").writeNullable[String] and
    (JsPath \ "organization").write[String] and
    (JsPath \ "modules").write[Iterable[Module]]
  )(unlift(Repo.unapply))
  
  object lenses {
    val header: Lens[Repo,Option[String]] = LensImpl[Repo,Option[String]]("header", _.header, (d,v) => d.copy(header = v))
    val organization: Lens[Repo,String] = LensImpl[Repo,String]("organization", _.organization, (d,v) => d.copy(organization = v))
    val modules: Lens[Repo,Iterable[Module]] = LensImpl[Repo,Iterable[Module]]("modules", _.modules, (d,v) => d.copy(modules = v))
  }
  
  val allLenses = List(lenses.header,lenses.organization,lenses.modules)
  
  val allLensesHList = lenses.header :: lenses.organization :: lenses.modules :: shapeless.HNil

}
    



trait MxModule {

  implicit val jsonReads: Reads[Module] = (
    (JsPath \ "sbtName").read[String] and
    (JsPath \ "projectType").readNullable[String] and
    (JsPath \ "artifactName").readNullable[String] and
    (JsPath \ "directory").readNullable[String] and
    (JsPath \ "dependsOn").xreadWithDefault[Iterable[String]](Nil) and
    (JsPath \ "dependencies").readNullable[String] and
    (JsPath \ "jvmDependencies").readNullable[String] and
    (JsPath \ "jsDependencies").readNullable[String] and
    (JsPath \ "extraSettings").readNullable[String]
  )(Module.apply _)
  
  implicit val jsonWrites: Writes[Module] = (
    (JsPath \ "sbtName").write[String] and
    (JsPath \ "projectType").writeNullable[String] and
    (JsPath \ "artifactName").writeNullable[String] and
    (JsPath \ "directory").writeNullable[String] and
    (JsPath \ "dependsOn").write[Iterable[String]] and
    (JsPath \ "dependencies").writeNullable[String] and
    (JsPath \ "jvmDependencies").writeNullable[String] and
    (JsPath \ "jsDependencies").writeNullable[String] and
    (JsPath \ "extraSettings").writeNullable[String]
  )(unlift(Module.unapply))
  
  object lenses {
    val sbtName: Lens[Module,String] = LensImpl[Module,String]("sbtName", _.sbtName, (d,v) => d.copy(sbtName = v))
    val projectType: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("projectType", _.projectType, (d,v) => d.copy(projectType = v))
    val artifactName: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("artifactName", _.artifactName, (d,v) => d.copy(artifactName = v))
    val directory: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("directory", _.directory, (d,v) => d.copy(directory = v))
    val dependsOn: Lens[Module,Iterable[String]] = LensImpl[Module,Iterable[String]]("dependsOn", _.dependsOn, (d,v) => d.copy(dependsOn = v))
    val dependencies: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("dependencies", _.dependencies, (d,v) => d.copy(dependencies = v))
    val jvmDependencies: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("jvmDependencies", _.jvmDependencies, (d,v) => d.copy(jvmDependencies = v))
    val jsDependencies: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("jsDependencies", _.jsDependencies, (d,v) => d.copy(jsDependencies = v))
    val extraSettings: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("extraSettings", _.extraSettings, (d,v) => d.copy(extraSettings = v))
  }
  
  val allLenses = List(lenses.sbtName,lenses.projectType,lenses.artifactName,lenses.directory,lenses.dependsOn,lenses.dependencies,lenses.jvmDependencies,lenses.jsDependencies,lenses.extraSettings)
  
  val allLensesHList = lenses.sbtName :: lenses.projectType :: lenses.artifactName :: lenses.directory :: lenses.dependsOn :: lenses.dependencies :: lenses.jvmDependencies :: lenses.jsDependencies :: lenses.extraSettings :: shapeless.HNil

}
    



trait MxDependency {

  implicit val jsonReads: Reads[Dependency] = (
    (JsPath \ "organization").read[String] and
    (JsPath \ "scalaArtifactSeparator").read[String] and
    (JsPath \ "artifactName").read[String] and
    (JsPath \ "version").read[Identifier] and
    (JsPath \ "configuration").xreadNullableWithDefault[String](None) and
    (JsPath \ "exclusions").xreadWithDefault[Iterable[(String, String)]](Nil)
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
    