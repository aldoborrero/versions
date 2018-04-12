package a8.versions

import java.io.InputStream
import java.io.PrintStream
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.jar.JarFile

import net.model3.io.Pipe
import net.model3.lang.ClassX
import net.model3.newfile.Directory
import net.model3.newfile.File
import net.model3.newfile.Path

import predef._

object WebappExploder extends ImplicitLogging {

  private val WebappPrefix = "webapp/"

  private val jarFilePrefix = "jar:file:"
  private val filePrefix = "file:"
//
//  def explodeFromClasspath(localSource: Option[Directory], target: Directory): Unit = {
//
//    logger.debug("started explosion")
//
//    target.deleteTree()
//    target.makeDirectories()
//
//    val tracking = target.getParent.file("tracking.txt").createPrintStream()
//
//    localSource.foreach(explodeSingleDirectory(_, target, tracking))
//
//    ClassX.getResources(WebappPrefix)
//      .asScala
//      .foreach { url => explodeFromSingleUrl(url, target, tracking) }
//
//
//    tracking.close()
//
//    logger.debug("finished explosion")
//  }

  def explodeFromLibDirectory(libDirectory: Directory, target: Directory, checkForPublicDescriptor: Boolean = true): Unit = {
    explodeEntries(
      libDirectory.files().asScala.map(_.getCanonicalPath).map(m3.fs.file),
      target,
      checkForPublicDescriptor
    )
  }

  def explodeEntries(classpathEntries: Iterable[m3.fs.FileSystem#Path], target: Directory, checkForPublicDescriptor: Boolean = true): Unit = {

    logger.debug("started explosion")

    target.deleteTree()
    target.makeDirectories()

    val tracking = target.getParent.file("tracking.txt").createPrintStream()

    classpathEntries
      .foreach(f => explodeFromSingleUrl(new java.io.File(f.canonicalPath).toURI.toURL, target, tracking, checkForPublicDescriptor))

    tracking.close()

  }

  def explodeFromSingleUrl(url: URL, target: Directory, tracking: PrintStream, checkForPublicDescriptor: Boolean = true): Unit = {

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
            logger.debug(s"no ${WebappPrefix} folder in ${jarFilename}")
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

    if ( urlstr.startsWith(jarFilePrefix) ) {
      val jarFilename = urlstr.split("!/")(0).substring(jarFilePrefix.length)
      processJarFile(jarFilename)
    } else if ( urlAsFile.exists(file => file.isFile && file.toPath.endsWith(".jar")) ) {
      processJarFile(urlAsFile.get.getCanonicalPath)
    } else if ( urlAsFile.exists(_.isDirectory) ) {
      val dir = new Directory(url)
      WebappExploder.explodeSingleDirectory(dir, target, tracking)

    } else {
      logger.warn(s"don't know how to handle ${url}")
    }
  }

  def explodeSingleDirectory(root: Directory, target: Directory, tracking: PrintStream): Unit = {
    logger.debug("exploding directory " + root.getCanonicalPath)
    copy(root.getCanonicalPath, root, target, target, tracking)
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
    from.files.asScala.foreach(f => copy(source, f.createInputStream(), to.file(f.getName), target, tracking))
    from.subdirs.asScala.foreach(d => copy(source, d, to.subdir(d.getName), target, tracking))
  }

  private def copy(source: String, in: InputStream, toFile: File, target: Directory, tracking: PrintStream) {
    val path = toFile.getPathRelativeTo(target)

    if (toFile.exists) {
      logger.warn(s"resource ${path} already exists will ignore that resource from ${source}")
    } else {
      addTracking(source, toFile, target, tracking)
      new Pipe(in, toFile.createOutputStream).process()
    }
  }

  private def makeDirectory(source: String, to: Directory, target: Directory, tracking: PrintStream): Unit = {
    if (!to.exists) {
      to.makeDirectory
      addTracking(source, to, target, tracking)
    }
  }

  private def addTracking(source: String, path: Path[_], target: Directory, tracking: PrintStream) {
    val relativePath = "/" + path.getPathRelativeTo(target)
    tracking.println(relativePath + "  --  " + source)
  }

}
