package io.accur8.neodeploy

import a8.shared.FileSystem.{File, Path}
import a8.shared.app.LoggingF
import zio.{Task, ZIO}

import java.nio.file.Files

object PathAssist extends LoggingF {

  def symlink(target: Path, link: File): Task[Unit] =
    ZIO.attemptBlocking {
      Files.createSymbolicLink(link.asNioPath, target.asNioPath)
    }

}
