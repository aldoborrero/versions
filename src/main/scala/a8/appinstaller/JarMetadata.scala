package a8.appinstaller


import java.io.ByteArrayInputStream
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.{Paths, Files}

import m3.json.JsonToString
import m3.predef._
import m3.fs._
import net.model3.lang.SystemX
import org.apache.tools.ant.DirectoryScanner
import predef._

object JarMetadata {

  def process(appDir: Directory, jarFile: File): Unit = {
    ZipAssist.readEntryFromZipFile(jarFile, "META-INF/a8-deployer.json").foreach { json =>
      val jv = parseHocon(json)
      jv.deserialize[JarMetadata].apply(appDir)
    }
  }

  case class Explode(
    jar: String,
    includes: Option[String]
  ) extends JsonToString
  
  case class SymLink(
    link: String,
    target: String
  ) extends JsonToString

}



case class JarMetadata(
  explode: List[JarMetadata.Explode],
  chmod_exec: List[String],
  symlinks: List[JarMetadata.SymLink]
) extends Logging {

  implicit class ListProcessor[A](l: List[A]) {
    def foreachx(fn: A=>Unit) = {
      l.foreach { i =>
        try {
          logger.debug(s"processing ${i}")
          fn(i)
        } catch {
          case e: Exception =>
            logger.warn(s"error processing ${i}", e)
        }
      }
    }
  }

  def apply(appDir: Directory) = {

    val libDir = appDir \\ "lib"
    val metaInfDir = appDir \\ "META-INF"

    def exploder() = {
      explode.foreachx { x =>
        val scanner = new DirectoryScanner
        scanner.setIncludes(Array(x.jar))
        scanner.setBasedir(libDir.canonicalPath)
        scanner.setCaseSensitive(false)
        scanner.scan()
        scanner.getIncludedFiles.foreach { fs =>
          val f = libDir \ fs
         logger.debug("exploding " + f.canonicalPath)
          ZipAssist.unzip(f, appDir, x.includes)
        }
      }
    }

    def chmoder() = if (!SystemX.isWindows) {
      chmod_exec.foreachx { x =>
        val scanner = new DirectoryScanner
        scanner.setIncludes(Array(x))
        scanner.setBasedir(appDir.canonicalPath)
        scanner.setCaseSensitive(false)
        scanner.scan()
        scanner.getIncludedFiles.foreach { fs =>
          val f = appDir \ fs
          val path = Paths.get(f.canonicalPath)
          val perms = Files.getPosixFilePermissions(path)
          perms.add(PosixFilePermission.OWNER_EXECUTE)
          perms.add(PosixFilePermission.GROUP_EXECUTE)
          Files.setPosixFilePermissions(path, perms)
        }
      }
    }

    def symlinker() = {
      symlinks.foreachx { symlink =>
        val target = Paths.get(symlink.target)
        val link = Paths.get((appDir \ symlink.link).canonicalPath)

        Files.createSymbolicLink(link, target)
      }
    }

    exploder()
    chmoder()
    symlinker()

    metaInfDir.deleteTree()

  }

}
