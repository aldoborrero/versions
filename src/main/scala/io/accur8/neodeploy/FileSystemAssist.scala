package io.accur8.neodeploy


import a8.shared.FileSystem
import java.nio.file.Paths
import java.nio.file.Files
import a8.shared.SharedImports._
import zio.ZIO
import a8.shared.app.LoggingF
import PredefAssist._

object FileSystemAssist extends LoggingF {

  case class FileSet(root: FileSystem.Directory, paths: Vector[FileSystem.Path] = Vector.empty) {
    private val rootPathStr = root.asNioPath.toFile().getAbsolutePath()
    def addPath(pathStr: String, mustExist: Boolean = true): FileSet = {
      val nioPath: java.nio.file.Path = Paths.get(rootPathStr, pathStr)
      val pathAsFile = nioPath.toFile()
      val absolutePathStr = pathAsFile.getAbsolutePath()
      val resolvedPathOpt: Option[FileSystem.Path] = 
        if ( pathAsFile.isDirectory() ) {
          FileSystem.dir(absolutePathStr).some
        } else if ( pathAsFile.isFile() ) {
          FileSystem.file(absolutePathStr).some
        } else if ( pathAsFile.exists() ) {
          sys.error(s"path ${absolutePathStr} is neither a file or directory")
        } else if ( mustExist ) {
          sys.error(s"path ${absolutePathStr} does not exist")
        } else {
          None
        }
      
      resolvedPathOpt match {
        case Some(resolvedPath) =>
          // validate this path is child of the root
          resolvedPath.relativeTo(root)
          copy(paths = paths :+ resolvedPath)
        case None => 
          this
      }
    }

    def copyTo(target: FileSystem.Directory): zio.Task[Unit] = 
      ZIO.attemptBlocking {
        paths
          .foreach {
            case f: FileSystem.File =>
              val targetFile = target.file(f.relativeTo(root))
              targetFile.parent.resolve
              logger.debug(z"copying file ${f} --> ${targetFile}") 
              f.copyTo(targetFile)
            case d: FileSystem.Directory =>
              val targetDir = target.subdir(d.relativeTo(root)).parentOpt.get
              if ( !targetDir.exists() )
                targetDir.makeDirectories()
              logger.debug(z"copying directory ${d} --> ${targetDir}")
              d.copyTo(targetDir)
          }
      }

  }

}
