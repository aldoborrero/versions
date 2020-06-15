package a8.appinstaller

import a8.appinstaller.AppInstallerConfig.LibDirKind

object LibDirKindDemo extends App {


  val values: List[Option[String]] = List("Copy", "copy", "xCopy").map(Some(_))

  runDemo(values)

  def runDemo(args: List[Option[String]]): Unit = {
    args.foreach { k =>
      val kind: Option[LibDirKind] =
        k
          .flatMap{ kk =>
            val result: Option[LibDirKind] = LibDirKind
              .values
              .find(_.entryName.equalsIgnoreCase(kk))
            if (result.isEmpty) {
              sys.error(s"libDirKind entered does not match case insensitive value in ${LibDirKind.values.map(_.entryName).mkString("['", "', '", "']")}")
            }
            result
          }
          .orElse(Some(LibDirKind.Symlink))
      println(s"kind=${kind}")
    }
  }

}
