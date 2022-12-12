package example


import io.accur8.neodeploy.systemstate.SystemStateModel._

object PermsDemo extends App {

//  println(UnixPerms.parse("0644"))
//  println(UnixPerms.parse("777").mkString("\n"))
  println(UnixPerms.parse("0644").mkString("\n"))

}
