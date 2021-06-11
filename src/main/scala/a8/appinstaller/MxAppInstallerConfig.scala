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
import a8.appinstaller.AppInstallerConfig.LibDirKind
//====


object MxAppInstallerConfig {
  
  trait MxAppInstallerConfig {
  
    implicit lazy val jsonReads: Reads[AppInstallerConfig] =
      JsonAssist.utils.lazyReads((
        (JsPath \ "organization").read[String] and
        (JsPath \ "artifact").read[String] and
        (JsPath \ "version").read[String] and
        (JsPath \ "branch").readNullable[String] and
        (JsPath \ "installDir").xreadNullableWithDefault[String](None) and
        (JsPath \ "libDirKind").xreadNullableWithDefault[LibDirKind](None) and
        (JsPath \ "webappExplode").xreadNullableWithDefault[Boolean](None
        )
      )(AppInstallerConfig.apply _))
    
    implicit lazy val jsonWrites: OWrites[AppInstallerConfig] =
      JsonAssist.utils.lazyOWrites((
        (JsPath \ "organization").write[String] and
        (JsPath \ "artifact").write[String] and
        (JsPath \ "version").write[String] and
        (JsPath \ "branch").writeNullable[String] and
        (JsPath \ "installDir").writeNullable[String] and
        (JsPath \ "libDirKind").writeNullable[LibDirKind] and
        (JsPath \ "webappExplode").writeNullable[Boolean]
      )(unlift(AppInstallerConfig.unapply)))
    
    lazy val jsonFormat = JsonAssist.utils.lazyFormat(Format(jsonReads, jsonWrites))
    
    object lenses {
      lazy val organization: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("organization", _.organization, (d,v) => d.copy(organization = v))
      lazy val artifact: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("artifact", _.artifact, (d,v) => d.copy(artifact = v))
      lazy val version: Lens[AppInstallerConfig,String] = LensImpl[AppInstallerConfig,String]("version", _.version, (d,v) => d.copy(version = v))
      lazy val branch: Lens[AppInstallerConfig,Option[String]] = LensImpl[AppInstallerConfig,Option[String]]("branch", _.branch, (d,v) => d.copy(branch = v))
      lazy val installDir: Lens[AppInstallerConfig,Option[String]] = LensImpl[AppInstallerConfig,Option[String]]("installDir", _.installDir, (d,v) => d.copy(installDir = v))
      lazy val libDirKind: Lens[AppInstallerConfig,Option[LibDirKind]] = LensImpl[AppInstallerConfig,Option[LibDirKind]]("libDirKind", _.libDirKind, (d,v) => d.copy(libDirKind = v))
      lazy val webappExplode: Lens[AppInstallerConfig,Option[Boolean]] = LensImpl[AppInstallerConfig,Option[Boolean]]("webappExplode", _.webappExplode, (d,v) => d.copy(webappExplode = v))
    }
    
    object parameters {
      lazy val organization: CaseClassParm[AppInstallerConfig,String] = CaseClassParm[AppInstallerConfig,String]("organization", lenses.organization, None, 0)
      lazy val artifact: CaseClassParm[AppInstallerConfig,String] = CaseClassParm[AppInstallerConfig,String]("artifact", lenses.artifact, None, 1)
      lazy val version: CaseClassParm[AppInstallerConfig,String] = CaseClassParm[AppInstallerConfig,String]("version", lenses.version, None, 2)
      lazy val branch: CaseClassParm[AppInstallerConfig,Option[String]] = CaseClassParm[AppInstallerConfig,Option[String]]("branch", lenses.branch, None, 3)
      lazy val installDir: CaseClassParm[AppInstallerConfig,Option[String]] = CaseClassParm[AppInstallerConfig,Option[String]]("installDir", lenses.installDir, Some(()=> None), 4)
      lazy val libDirKind: CaseClassParm[AppInstallerConfig,Option[LibDirKind]] = CaseClassParm[AppInstallerConfig,Option[LibDirKind]]("libDirKind", lenses.libDirKind, Some(()=> None), 5)
      lazy val webappExplode: CaseClassParm[AppInstallerConfig,Option[Boolean]] = CaseClassParm[AppInstallerConfig,Option[Boolean]]("webappExplode", lenses.webappExplode, Some(()=> None
      ), 6)
    }
    
    
    object unsafe {
      def rawConstruct(values: IndexedSeq[Any]): AppInstallerConfig = {
        AppInstallerConfig(
          organization = values(0).asInstanceOf[String],
          artifact = values(1).asInstanceOf[String],
          version = values(2).asInstanceOf[String],
          branch = values(3).asInstanceOf[Option[String]],
          installDir = values(4).asInstanceOf[Option[String]],
          libDirKind = values(5).asInstanceOf[Option[LibDirKind]],
          webappExplode = values(6).asInstanceOf[Option[Boolean]],
        )
      }
      def typedConstruct(organization: String, artifact: String, version: String, branch: Option[String], installDir: Option[String], libDirKind: Option[LibDirKind], webappExplode: Option[Boolean]): AppInstallerConfig =
        AppInstallerConfig(organization, artifact, version, branch, installDir, libDirKind, webappExplode)
    
    }
    
    
    lazy val allLenses = List(lenses.organization,lenses.artifact,lenses.version,lenses.branch,lenses.installDir,lenses.libDirKind,lenses.webappExplode)
    
    lazy val allLensesHList = lenses.organization :: lenses.artifact :: lenses.version :: lenses.branch :: lenses.installDir :: lenses.libDirKind :: lenses.webappExplode :: shapeless.HNil
    
    lazy val allParametersHList = parameters.organization :: parameters.artifact :: parameters.version :: parameters.branch :: parameters.installDir :: parameters.libDirKind :: parameters.webappExplode :: shapeless.HNil
    
    lazy val typeName = "AppInstallerConfig"
  
  }
}
