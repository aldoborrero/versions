package a8.appinstaller


import m3.json.JsonAssist
import m3.predef._
import m3.fs._
import java.io.{ File => JFile }
import language.implicitConversions

object predef extends JsonAssist with Logging {

  implicit def serializer = inject[m3.json.Serialization.Serializer]

  implicit def toJavaIoFile(p: FileSystem#Path): JFile = new JFile(p.canonicalPath)
  implicit def toM3FsFile(f: JFile): m3.fs.File = file(f.getAbsolutePath)
  implicit def toM3FsDir(f: JFile): m3.fs.Directory = dir(f.getAbsolutePath)

}
