package io.accur8.neodeploy

import a8.shared.FileSystem.{File, Path}
import zio.{Task, ZIO}

import java.nio.file.Files

object PathAssist {


  def symlink(target: Path, link: File): Task[Unit] =
    ZIO.attemptBlocking {
      try {
        Files.createSymbolicLink(link.asNioPath, target.asNioPath)
      } catch {
        case e: Exception =>
          e.toString
      }
    }

}
