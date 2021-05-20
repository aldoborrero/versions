package a8.versions


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
import JsonFormats._
import ast._
//====


object Mxast {
  
  trait MxRepo {
  
    implicit lazy val jsonReads: Reads[Repo] =
      JsonAssist.utils.lazyReads((
        (JsPath \ "header").xreadNullableWithDefault[String](None) and
        (JsPath \ "organization").read[String] and
        (JsPath \ "gradle").xreadWithDefault[Boolean](false) and
        (JsPath \ "modules").read[Iterable[Module]]
      )(Repo.apply _))
    
    implicit lazy val jsonWrites: OWrites[Repo] =
      JsonAssist.utils.lazyOWrites((
        (JsPath \ "header").writeNullable[String] and
        (JsPath \ "organization").write[String] and
        (JsPath \ "gradle").write[Boolean] and
        (JsPath \ "modules").write[Iterable[Module]]
      )(unlift(Repo.unapply)))
    
    lazy val jsonFormat = JsonAssist.utils.lazyFormat(Format(jsonReads, jsonWrites))
    
    object lenses {
      lazy val header: Lens[Repo,Option[String]] = LensImpl[Repo,Option[String]]("header", _.header, (d,v) => d.copy(header = v))
      lazy val organization: Lens[Repo,String] = LensImpl[Repo,String]("organization", _.organization, (d,v) => d.copy(organization = v))
      lazy val gradle: Lens[Repo,Boolean] = LensImpl[Repo,Boolean]("gradle", _.gradle, (d,v) => d.copy(gradle = v))
      lazy val modules: Lens[Repo,Iterable[Module]] = LensImpl[Repo,Iterable[Module]]("modules", _.modules, (d,v) => d.copy(modules = v))
    }
    
    object parameters {
      lazy val header: CaseClassParm[Repo,Option[String]] = CaseClassParm[Repo,Option[String]]("header", lenses.header, Some(()=> None), 0)
      lazy val organization: CaseClassParm[Repo,String] = CaseClassParm[Repo,String]("organization", lenses.organization, None, 1)
      lazy val gradle: CaseClassParm[Repo,Boolean] = CaseClassParm[Repo,Boolean]("gradle", lenses.gradle, Some(()=> false), 2)
      lazy val modules: CaseClassParm[Repo,Iterable[Module]] = CaseClassParm[Repo,Iterable[Module]]("modules", lenses.modules, None, 3)
    }
    
    
    object unsafe {
      def rawConstruct(values: IndexedSeq[Any]): Repo = {
        Repo(
          header = values(0).asInstanceOf[Option[String]],
          organization = values(1).asInstanceOf[String],
          gradle = values(2).asInstanceOf[Boolean],
          modules = values(3).asInstanceOf[Iterable[Module]],
        )
      }
      def typedConstruct(header: Option[String], organization: String, gradle: Boolean, modules: Iterable[Module]): Repo =
        Repo(header, organization, gradle, modules)
    
    }
    
    
    lazy val allLenses = List(lenses.header,lenses.organization,lenses.gradle,lenses.modules)
    
    lazy val allLensesHList = lenses.header :: lenses.organization :: lenses.gradle :: lenses.modules :: shapeless.HNil
    
    lazy val allParametersHList = parameters.header :: parameters.organization :: parameters.gradle :: parameters.modules :: shapeless.HNil
    
    lazy val typeName = "Repo"
  
  }
  
  
  
  
  trait MxModule {
  
    implicit lazy val jsonReads: Reads[Module] =
      JsonAssist.utils.lazyReads((
        (JsPath \ "sbtName").read[String] and
        (JsPath \ "projectType").readNullable[String] and
        (JsPath \ "artifactName").readNullable[String] and
        (JsPath \ "directory").readNullable[String] and
        (JsPath \ "dependsOn").xreadWithDefault[Iterable[String]](Nil) and
        (JsPath \ "dependencies").readNullable[String] and
        (JsPath \ "jvmDependencies").readNullable[String] and
        (JsPath \ "jsDependencies").readNullable[String] and
        (JsPath \ "extraSettings").readNullable[String]
      )(Module.apply _))
    
    implicit lazy val jsonWrites: OWrites[Module] =
      JsonAssist.utils.lazyOWrites((
        (JsPath \ "sbtName").write[String] and
        (JsPath \ "projectType").writeNullable[String] and
        (JsPath \ "artifactName").writeNullable[String] and
        (JsPath \ "directory").writeNullable[String] and
        (JsPath \ "dependsOn").write[Iterable[String]] and
        (JsPath \ "dependencies").writeNullable[String] and
        (JsPath \ "jvmDependencies").writeNullable[String] and
        (JsPath \ "jsDependencies").writeNullable[String] and
        (JsPath \ "extraSettings").writeNullable[String]
      )(unlift(Module.unapply)))
    
    lazy val jsonFormat = JsonAssist.utils.lazyFormat(Format(jsonReads, jsonWrites))
    
    object lenses {
      lazy val sbtName: Lens[Module,String] = LensImpl[Module,String]("sbtName", _.sbtName, (d,v) => d.copy(sbtName = v))
      lazy val projectType: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("projectType", _.projectType, (d,v) => d.copy(projectType = v))
      lazy val artifactName: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("artifactName", _.artifactName, (d,v) => d.copy(artifactName = v))
      lazy val directory: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("directory", _.directory, (d,v) => d.copy(directory = v))
      lazy val dependsOn: Lens[Module,Iterable[String]] = LensImpl[Module,Iterable[String]]("dependsOn", _.dependsOn, (d,v) => d.copy(dependsOn = v))
      lazy val dependencies: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("dependencies", _.dependencies, (d,v) => d.copy(dependencies = v))
      lazy val jvmDependencies: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("jvmDependencies", _.jvmDependencies, (d,v) => d.copy(jvmDependencies = v))
      lazy val jsDependencies: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("jsDependencies", _.jsDependencies, (d,v) => d.copy(jsDependencies = v))
      lazy val extraSettings: Lens[Module,Option[String]] = LensImpl[Module,Option[String]]("extraSettings", _.extraSettings, (d,v) => d.copy(extraSettings = v))
    }
    
    object parameters {
      lazy val sbtName: CaseClassParm[Module,String] = CaseClassParm[Module,String]("sbtName", lenses.sbtName, None, 0)
      lazy val projectType: CaseClassParm[Module,Option[String]] = CaseClassParm[Module,Option[String]]("projectType", lenses.projectType, None, 1)
      lazy val artifactName: CaseClassParm[Module,Option[String]] = CaseClassParm[Module,Option[String]]("artifactName", lenses.artifactName, None, 2)
      lazy val directory: CaseClassParm[Module,Option[String]] = CaseClassParm[Module,Option[String]]("directory", lenses.directory, None, 3)
      lazy val dependsOn: CaseClassParm[Module,Iterable[String]] = CaseClassParm[Module,Iterable[String]]("dependsOn", lenses.dependsOn, Some(()=> Nil), 4)
      lazy val dependencies: CaseClassParm[Module,Option[String]] = CaseClassParm[Module,Option[String]]("dependencies", lenses.dependencies, None, 5)
      lazy val jvmDependencies: CaseClassParm[Module,Option[String]] = CaseClassParm[Module,Option[String]]("jvmDependencies", lenses.jvmDependencies, None, 6)
      lazy val jsDependencies: CaseClassParm[Module,Option[String]] = CaseClassParm[Module,Option[String]]("jsDependencies", lenses.jsDependencies, None, 7)
      lazy val extraSettings: CaseClassParm[Module,Option[String]] = CaseClassParm[Module,Option[String]]("extraSettings", lenses.extraSettings, None, 8)
    }
    
    
    object unsafe {
      def rawConstruct(values: IndexedSeq[Any]): Module = {
        Module(
          sbtName = values(0).asInstanceOf[String],
          projectType = values(1).asInstanceOf[Option[String]],
          artifactName = values(2).asInstanceOf[Option[String]],
          directory = values(3).asInstanceOf[Option[String]],
          dependsOn = values(4).asInstanceOf[Iterable[String]],
          dependencies = values(5).asInstanceOf[Option[String]],
          jvmDependencies = values(6).asInstanceOf[Option[String]],
          jsDependencies = values(7).asInstanceOf[Option[String]],
          extraSettings = values(8).asInstanceOf[Option[String]],
        )
      }
      def typedConstruct(sbtName: String, projectType: Option[String], artifactName: Option[String], directory: Option[String], dependsOn: Iterable[String], dependencies: Option[String], jvmDependencies: Option[String], jsDependencies: Option[String], extraSettings: Option[String]): Module =
        Module(sbtName, projectType, artifactName, directory, dependsOn, dependencies, jvmDependencies, jsDependencies, extraSettings)
    
    }
    
    
    lazy val allLenses = List(lenses.sbtName,lenses.projectType,lenses.artifactName,lenses.directory,lenses.dependsOn,lenses.dependencies,lenses.jvmDependencies,lenses.jsDependencies,lenses.extraSettings)
    
    lazy val allLensesHList = lenses.sbtName :: lenses.projectType :: lenses.artifactName :: lenses.directory :: lenses.dependsOn :: lenses.dependencies :: lenses.jvmDependencies :: lenses.jsDependencies :: lenses.extraSettings :: shapeless.HNil
    
    lazy val allParametersHList = parameters.sbtName :: parameters.projectType :: parameters.artifactName :: parameters.directory :: parameters.dependsOn :: parameters.dependencies :: parameters.jvmDependencies :: parameters.jsDependencies :: parameters.extraSettings :: shapeless.HNil
    
    lazy val typeName = "Module"
  
  }
  
  
  
  
  trait MxDependency {
  
    implicit lazy val jsonReads: Reads[Dependency] =
      JsonAssist.utils.lazyReads((
        (JsPath \ "organization").read[String] and
        (JsPath \ "scalaArtifactSeparator").read[String] and
        (JsPath \ "artifactName").read[String] and
        (JsPath \ "version").read[Identifier] and
        (JsPath \ "configuration").xreadNullableWithDefault[String](None) and
        (JsPath \ "exclusions").xreadWithDefault[Iterable[(String, String)]](Nil)
      )(Dependency.apply _))
    
    implicit lazy val jsonWrites: OWrites[Dependency] =
      JsonAssist.utils.lazyOWrites((
        (JsPath \ "organization").write[String] and
        (JsPath \ "scalaArtifactSeparator").write[String] and
        (JsPath \ "artifactName").write[String] and
        (JsPath \ "version").write[Identifier] and
        (JsPath \ "configuration").writeNullable[String] and
        (JsPath \ "exclusions").write[Iterable[(String, String)]]
      )(unlift(Dependency.unapply)))
    
    lazy val jsonFormat = JsonAssist.utils.lazyFormat(Format(jsonReads, jsonWrites))
    
    object lenses {
      lazy val organization: Lens[Dependency,String] = LensImpl[Dependency,String]("organization", _.organization, (d,v) => d.copy(organization = v))
      lazy val scalaArtifactSeparator: Lens[Dependency,String] = LensImpl[Dependency,String]("scalaArtifactSeparator", _.scalaArtifactSeparator, (d,v) => d.copy(scalaArtifactSeparator = v))
      lazy val artifactName: Lens[Dependency,String] = LensImpl[Dependency,String]("artifactName", _.artifactName, (d,v) => d.copy(artifactName = v))
      lazy val version: Lens[Dependency,Identifier] = LensImpl[Dependency,Identifier]("version", _.version, (d,v) => d.copy(version = v))
      lazy val configuration: Lens[Dependency,Option[String]] = LensImpl[Dependency,Option[String]]("configuration", _.configuration, (d,v) => d.copy(configuration = v))
      lazy val exclusions: Lens[Dependency,Iterable[(String, String)]] = LensImpl[Dependency,Iterable[(String, String)]]("exclusions", _.exclusions, (d,v) => d.copy(exclusions = v))
    }
    
    object parameters {
      lazy val organization: CaseClassParm[Dependency,String] = CaseClassParm[Dependency,String]("organization", lenses.organization, None, 0)
      lazy val scalaArtifactSeparator: CaseClassParm[Dependency,String] = CaseClassParm[Dependency,String]("scalaArtifactSeparator", lenses.scalaArtifactSeparator, None, 1)
      lazy val artifactName: CaseClassParm[Dependency,String] = CaseClassParm[Dependency,String]("artifactName", lenses.artifactName, None, 2)
      lazy val version: CaseClassParm[Dependency,Identifier] = CaseClassParm[Dependency,Identifier]("version", lenses.version, None, 3)
      lazy val configuration: CaseClassParm[Dependency,Option[String]] = CaseClassParm[Dependency,Option[String]]("configuration", lenses.configuration, Some(()=> None), 4)
      lazy val exclusions: CaseClassParm[Dependency,Iterable[(String, String)]] = CaseClassParm[Dependency,Iterable[(String, String)]]("exclusions", lenses.exclusions, Some(()=> Nil), 5)
    }
    
    
    object unsafe {
      def rawConstruct(values: IndexedSeq[Any]): Dependency = {
        Dependency(
          organization = values(0).asInstanceOf[String],
          scalaArtifactSeparator = values(1).asInstanceOf[String],
          artifactName = values(2).asInstanceOf[String],
          version = values(3).asInstanceOf[Identifier],
          configuration = values(4).asInstanceOf[Option[String]],
          exclusions = values(5).asInstanceOf[Iterable[(String, String)]],
        )
      }
      def typedConstruct(organization: String, scalaArtifactSeparator: String, artifactName: String, version: Identifier, configuration: Option[String], exclusions: Iterable[(String, String)]): Dependency =
        Dependency(organization, scalaArtifactSeparator, artifactName, version, configuration, exclusions)
    
    }
    
    
    lazy val allLenses = List(lenses.organization,lenses.scalaArtifactSeparator,lenses.artifactName,lenses.version,lenses.configuration,lenses.exclusions)
    
    lazy val allLensesHList = lenses.organization :: lenses.scalaArtifactSeparator :: lenses.artifactName :: lenses.version :: lenses.configuration :: lenses.exclusions :: shapeless.HNil
    
    lazy val allParametersHList = parameters.organization :: parameters.scalaArtifactSeparator :: parameters.artifactName :: parameters.version :: parameters.configuration :: parameters.exclusions :: shapeless.HNil
    
    lazy val typeName = "Dependency"
  
  }
}
