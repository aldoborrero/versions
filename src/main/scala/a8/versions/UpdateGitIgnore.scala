package a8.versions

import m3.fs._

import scala.collection.immutable.List
import scala.collection.mutable.HashSet
import a8.common.CommonOps._

object UpdateGitIgnore {

  lazy val explicitFileTypesToIgnore = List[String](
    "/.logs/"
    , "/cache/"
    , "/logs/"
    , "/log-usage.txt"
    , "/tracking.txt"
    , "/webapp-exploded/"
    , "/webapp-composite/"
    , "/webapp-temp/"
    , "/target/"
    , "/*.iml"
    , "/.idea/"
    , "/dumpTemp/"
    , ".DS_Store"
    , "/hs_err_*.log"
    , "/src-deps/"
    , "developer.properties"
    , "/src/main/webapp/WEB-INF/source-jsp/"
    , "/config/"
    , "/.bloop/"
    , "/.metals/"
    , "/out/"
    , "developer.hocon"
  )

  lazy val userSuppliedFileTypesToIgnore: List[String] = {
    val f = RepositoryOps.userHome \\ ".a8" \ "gitignore.template"
    f.exists.option(f.readText.linesIterator.toList.filter(_.isNotBlank)).getOrElse(Nil)
  }

  lazy val fileTypesToIgnore = explicitFileTypesToIgnore ++ userSuppliedFileTypesToIgnore

  def update(gitIgnoreFile0: java.io.File): Unit = {
    val gitIgnoreFile = file(gitIgnoreFile0.getCanonicalPath)
    if (!gitIgnoreFile.exists) {
      gitIgnoreFile.write("")
    }
    println("updating:  " + gitIgnoreFile.canonicalPath)
    val text = Option(gitIgnoreFile.lines)

    text match {
      case None => {
        gitIgnoreFile.withWriter(out => {
          fileTypesToIgnore.foreach(s => out.println(s))
        })
      }
      case Some(lines) => {
        val genericAndSpecificIgnores = lines.distinct.splitAt(lines.indexWhere(s => s.equals("")))
        val genericIgnoreTypes = genericAndSpecificIgnores._1.sorted
        val specificFiles = genericAndSpecificIgnores._2.sorted

        var newGenericIgnoreTypes: HashSet[String] = new HashSet()
        genericIgnoreTypes.foreach(t => newGenericIgnoreTypes.+=(t))
        fileTypesToIgnore.foreach(ft => {
          newGenericIgnoreTypes.+=(ft)
        })

        gitIgnoreFile.withWriter{ out => 
          newGenericIgnoreTypes.foreach(s => out.println(s))
          out.println("")
          specificFiles.foreach(s => if (!s.equals("")) out.println(s))
        }
      }
    }
  }
}
