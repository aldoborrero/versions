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

//====


trait MxInstallInventory {

  implicit val jsonReads: Reads[InstallInventory] = (
    (JsPath \ "appInstallerConfig").read[AppInstallerConfig] and
    (JsPath \ "classpath").read[Iterable[String]]
  )(InstallInventory.apply _)
  
  implicit val jsonWrites: Writes[InstallInventory] = (
    (JsPath \ "appInstallerConfig").write[AppInstallerConfig] and
    (JsPath \ "classpath").write[Iterable[String]]
  )(unlift(InstallInventory.unapply))
  
  object lenses {
    val appInstallerConfig: Lens[InstallInventory,AppInstallerConfig] = LensImpl[InstallInventory,AppInstallerConfig]("appInstallerConfig", _.appInstallerConfig, (d,v) => d.copy(appInstallerConfig = v))
    val classpath: Lens[InstallInventory,Iterable[String]] = LensImpl[InstallInventory,Iterable[String]]("classpath", _.classpath, (d,v) => d.copy(classpath = v))
  }
  
  val allLenses = List(lenses.appInstallerConfig,lenses.classpath)
  
  val allLensesHList = lenses.appInstallerConfig :: lenses.classpath :: shapeless.HNil

}
    