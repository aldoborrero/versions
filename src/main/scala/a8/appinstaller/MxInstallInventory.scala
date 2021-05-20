package a8.appinstaller


import a8.common.Lenser.{Lens, LensImpl}
import play.api.libs.json.{JsPath, Reads, OWrites}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import a8.common.CommonOps._
import a8.common.JsonAssist
import a8.common.CaseClassParm

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====

//====


object MxInstallInventory {
  
  trait MxInstallInventory {
  
    implicit lazy val jsonReads: Reads[InstallInventory] =
      JsonAssist.utils.lazyReads((
        (JsPath \ "appInstallerConfig").read[AppInstallerConfig] and
        (JsPath \ "classpath").read[Iterable[String]]
      )(InstallInventory.apply _))
    
    implicit lazy val jsonWrites: OWrites[InstallInventory] =
      JsonAssist.utils.lazyOWrites((
        (JsPath \ "appInstallerConfig").write[AppInstallerConfig] and
        (JsPath \ "classpath").write[Iterable[String]]
      )(unlift(InstallInventory.unapply)))
    
    lazy val jsonFormat = JsonAssist.utils.lazyFormat(Format(jsonReads, jsonWrites))
    
    object lenses {
      lazy val appInstallerConfig: Lens[InstallInventory,AppInstallerConfig] = LensImpl[InstallInventory,AppInstallerConfig]("appInstallerConfig", _.appInstallerConfig, (d,v) => d.copy(appInstallerConfig = v))
      lazy val classpath: Lens[InstallInventory,Iterable[String]] = LensImpl[InstallInventory,Iterable[String]]("classpath", _.classpath, (d,v) => d.copy(classpath = v))
    }
    
    object parameters {
      lazy val appInstallerConfig: CaseClassParm[InstallInventory,AppInstallerConfig] = CaseClassParm[InstallInventory,AppInstallerConfig]("appInstallerConfig", lenses.appInstallerConfig, None, 0)
      lazy val classpath: CaseClassParm[InstallInventory,Iterable[String]] = CaseClassParm[InstallInventory,Iterable[String]]("classpath", lenses.classpath, None, 1)
    }
    
    
    object unsafe {
      def rawConstruct(values: IndexedSeq[Any]): InstallInventory = {
        InstallInventory(
          appInstallerConfig = values(0).asInstanceOf[AppInstallerConfig],
          classpath = values(1).asInstanceOf[Iterable[String]],
        )
      }
      def typedConstruct(appInstallerConfig: AppInstallerConfig, classpath: Iterable[String]): InstallInventory =
        InstallInventory(appInstallerConfig, classpath)
    
    }
    
    
    lazy val allLenses = List(lenses.appInstallerConfig,lenses.classpath)
    
    lazy val allLensesHList = lenses.appInstallerConfig :: lenses.classpath :: shapeless.HNil
    
    lazy val allParametersHList = parameters.appInstallerConfig :: parameters.classpath :: shapeless.HNil
    
    lazy val typeName = "InstallInventory"
  
  }
}
