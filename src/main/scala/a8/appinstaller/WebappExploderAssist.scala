package a8.appinstaller

import java.io.InputStream
import java.io.PrintStream
import java.net.URL
import java.util.jar.JarFile

import a8.versions.WebappExploder
import m3.predef._
import m3.fs._
import net.model3.newfile.Directory

import scala.collection.JavaConverters._

object WebappExploderAssist extends Logging {

  val WebappCompositeFolderName = "webapp-composite"

  private val jarFilePrefix = "jar:file:"
  private val filePrefix = "file:"

  private val WebappPrefix = "webapp/"

  def apply(installDir: m3.fs.Directory, jarFiles: Iterable[m3.fs.File]): Unit = {

    WebappExploder.explodeEntries(
      classpathEntries = jarFiles,
      target = new net.model3.newfile.Directory(installDir.subdir(WebappCompositeFolderName).canonicalPath),
    )

  }

}
