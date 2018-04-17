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
    (JsPath \ "appDir").xreadNullableWithDefault[String](None) and
    (JsPath \ "libDir").readNullable[LibDirKind] and
    (JsPath \ "webappExplode").xreadWithDefault[Boolean](true
    )
  )(AppInstallerConfig.apply _)
  
  implicit val jsonWrites: Writes[AppInstallerConfig] = (
    (JsPath \ "groupId").write[String] and
    (JsPath \ "artifactId").write[String] and
    (JsPath \ "version").write[String] and
    (JsPath \ "appDir").writeNullable[String] and
    (JsPath \ "libDir").writeNullable[LibDirKind] and
    (JsPath \ "webappExplode").write[Boolean]
  )(unlift(AppInstallerConfig.unapply))
  
  object lenses {
    val groupId: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("groupId", _.groupId, (d,v) => d.copy(groupId = v))
    val artifactId: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("artifactId", _.artifactId, (d,v) => d.copy(artifactId = v))
    val version: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("version", _.version, (d,v) => d.copy(version = v))
    val appDir: Lens[AppInstallerConfig,Option[String]] = LensImpl[AppInstallerConfig,Option[String]]("appDir", _.appDir, (d,v) => d.copy(appDir = v))
    val libDir: Lens[AppInstallerConfig,Option[LibDirKind]] = LensImpl[AppInstallerConfig,Option[LibDirKind]]("libDir", _.libDir, (d,v) => d.copy(libDir = v))
    val webappExplode: Lens[AppInstallerConfig,Boolean] = LensImpl[AppInstallerConfig,Boolean]("webappExplode", _.webappExplode, (d,v) => d.copy(webappExplode = v))
  }
  
  val allLenses = List(lenses.groupId,lenses.artifactId,lenses.version,lenses.appDir,lenses.libDir,lenses.webappExplode)
  
  val allLensesHList = lenses.groupId :: lenses.artifactId :: lenses.version :: lenses.appDir :: lenses.libDir :: lenses.webappExplode :: shapeless.HNil

}
    