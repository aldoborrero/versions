package a8.versions


import a8.shared.FileSystem
import FileSystem.Directory
import FileSystem.Path
import FileSystem.File

import java.io.InputStream
import java.io.PrintStream
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.jar.JarFile
import predef._

import java.io

object WebappExploder extends Logging {

  private val WebappPrefix = "webapp/"

  private val jarFilePrefix = "jar:file:"
  private val filePrefix = "file:"


//  def explodeFromLibDirectory(libDirectory: Directory, target: Directory, checkForPublicDescriptor: Boolean = true): Unit = {
//    explodeEntries(
//      libDirectory.files().asScala.map(_.getCanonicalPath).map(m3.fs.file),
//      target,
//      checkForPublicDescriptor
//    )
//  }

  def explodeEntries(classpathEntries: Iterable[FileSystem.Path], target: Directory, checkForPublicDescriptor: Boolean = true): Unit = {

    logger.debug("started explosion")

    if ( target.exists())
      target.delete()

    target.makeDirectories()

    target
      .parentOpt
      .get
      .file("tracking.txt")
      .withPrintStream { tracking =>
        classpathEntries
          .foreach(f => explodeFromSingleUrl(new java.io.File(f.canonicalPath).toURI.toURL, target, tracking, checkForPublicDescriptor))
      }

  }

  def explodeFromSingleUrl(url: URL, target: FileSystem.Directory, tracking: PrintStream, checkForPublicDescriptor: Boolean = true): Unit = {

    val urlstr = URLDecoder.decode(url.toString, StandardCharsets.UTF_8.toString)

    val urlAsFile =
      if ( urlstr.startsWith(filePrefix) ) Some(new java.io.File(url.toURI))
      else None

    def processJarFile(jarFilename: String) = {

      val jarFile = new JarFile(new java.io.File(jarFilename))
      try {
        val o = Option(jarFile.getEntry(WebappPrefix))
        Option(jarFile.getEntry(WebappPrefix)) match {
          case None =>
            // logger.trace(s"no ${WebappPrefix} folder in ${jarFilename}")
          case Some(_) =>
            WebappExploder.explodeSingleJar(jarFile, target, tracking)
        }
      } finally {
        try {
          jarFile.close()
        } catch {
          case e: Exception => logger.warn("error closing " + jarFilename, e)
        }
      }
    }

    val o1 = urlAsFile.map(_.isFile)
    val o2 = urlAsFile.map(_.getName.endsWith(".jar"))

    if ( urlstr.startsWith(jarFilePrefix) ) {
      val jarFilename = urlstr.split("!/")(0).substring(jarFilePrefix.length)
      processJarFile(jarFilename)
    } else if ( urlAsFile.exists(file => file.isFile && file.getName.endsWith(".jar")) ) {
      processJarFile(urlAsFile.get.getCanonicalPath)
    } else if ( urlAsFile.exists(_.isDirectory) ) {
      val jioFile = new java.io.File(url.toURI)
      val dir = FileSystem.dir(jioFile.getCanonicalPath)
      WebappExploder.explodeSingleDirectory(dir, target, tracking)

    } else {
      logger.warn(s"don't know how to handle ${url}")
    }
  }

  def explodeSingleDirectory(root: Directory, target: Directory, tracking: PrintStream): Unit = {
    logger.debug("exploding directory " + root.canonicalPath)
    copy(root.canonicalPath, root, target, target, tracking)
  }

  def explodeSingleJar(jarFile: JarFile, target: Directory, tracking: PrintStream): Unit = {
    val jarFilename = jarFile.getName
    logger.debug(s"exploding webapp folder in ${jarFilename}")

    jarFile.entries.asScala.foreach { je =>
      val jePath = je.getName

      try {
        if (jePath.startsWith(WebappPrefix)) {
          val path = jePath.substring(WebappPrefix.length)

          if (je.isDirectory) {
            makeDirectory(jarFilename, target.subdir(path), target, tracking)
          } else {
            copy(jarFilename, jarFile.getInputStream(je), target.file(path), target, tracking)
          }
        }
      } catch {
        case e: Exception => logger.warn("error processing " + jePath, e)
      }
    }

  }

  private def copy(source: String, from: Directory, to: Directory, target: Directory, tracking: PrintStream): Unit = {
    makeDirectory(source, to, target, tracking)
    from.files().foreach { f =>
      f.withInputStream { in =>
        copy(source, in, to.file(f.name), target, tracking)
      }
    }
    from.subdirs().foreach(d => copy(source, d, to.subdir(d.name), target, tracking))
  }

  private def copy(source: String, in: InputStream, toFile: File, target: Directory, tracking: PrintStream) = {
    val path = toFile.relativeTo(target)

    if (toFile.exists()) {
      logger.warn(s"resource ${path} already exists will ignore that resource from ${source}")
    } else {
      addTracking(source, toFile, target, tracking)
      toFile.withOutputStream { out =>
        val buffer = in.readAllBytes()
        out.write(buffer)
      }
    }
  }

  private def makeDirectory(source: String, to: Directory, target: Directory, tracking: PrintStream): Unit = {
    if (!to.exists()) {
      to.makeDirectory()
      addTracking(source, to, target, tracking)
    }
  }

  private def addTracking(source: String, path: Path, target: Directory, tracking: PrintStream) = {
    val relativePath = "/" + path.relativeTo(target)
    tracking.println(relativePath + "  --  " + source)
  }

}
