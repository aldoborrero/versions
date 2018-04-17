package a8.appinstaller


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
import a8.appinstaller.AppInstallerConfig.LibDirKind
//====


trait MxAppInstallerConfig {

  implicit val jsonReads: Reads[AppInstallerConfig] = (
    (JsPath \ "groupId").read[String] and
    (JsPath \ "artifactId").read[String] and
    (JsPath \ "version").read[String] and
    (JsPath \ "branch").readNullable[String] and
    (JsPath \ "installDir").xreadNullableWithDefault[String](None) and
    (JsPath \ "libDirKind").readNullable[LibDirKind] and
    (JsPath \ "webappExplode").readNullable[Boolean]
  )(AppInstallerConfig.apply _)
  
  implicit val jsonWrites: Writes[AppInstallerConfig] = (
    (JsPath \ "groupId").write[String] and
    (JsPath \ "artifactId").write[String] and
    (JsPath \ "version").write[String] and
    (JsPath \ "branch").writeNullable[String] and
    (JsPath \ "installDir").writeNullable[String] and
    (JsPath \ "libDirKind").writeNullable[LibDirKind] and
    (JsPath \ "webappExplode").writeNullable[Boolean]
  )(unlift(AppInstallerConfig.unapply))
  
  object lenses {
    val groupId: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("groupId", _.groupId, (d,v) => d.copy(groupId = v))
    val artifactId: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("artifactId", _.artifactId, (d,v) => d.copy(artifactId = v))
    val version: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("version", _.version, (d,v) => d.copy(version = v))
    val branch: Lens[AppInstallerConfig,Option[String]] = LensImpl[AppInstallerConfig,Option[String]]("branch", _.branch, (d,v) => d.copy(branch = v))
    val installDir: Lens[AppInstallerConfig,Option[String]] = LensImpl[AppInstallerConfig,Option[String]]("installDir", _.installDir, (d,v) => d.copy(installDir = v))
    val libDirKind: Lens[AppInstallerConfig,Option[LibDirKind]] = LensImpl[AppInstallerConfig,Option[LibDirKind]]("libDirKind", _.libDirKind, (d,v) => d.copy(libDirKind = v))
    val webappExplode: Lens[AppInstallerConfig,Option[Boolean]] = LensImpl[AppInstallerConfig,Option[Boolean]]("webappExplode", _.webappExplode, (d,v) => d.copy(webappExplode = v))
  }
  
  val allLenses = List(lenses.groupId,lenses.artifactId,lenses.version,lenses.branch,lenses.installDir,lenses.libDirKind,lenses.webappExplode)
  
  val allLensesHList = lenses.groupId :: lenses.artifactId :: lenses.version :: lenses.branch :: lenses.installDir :: lenses.libDirKind :: lenses.webappExplode :: shapeless.HNil

}
    