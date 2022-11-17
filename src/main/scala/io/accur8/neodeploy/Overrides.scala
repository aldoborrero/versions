package io.accur8.neodeploy



object Overrides {

  lazy val isLinux = {
    val osName = System.getProperty("os.name", "generic")
    osName.toLowerCase().contains("nux")
  }

  lazy val systemCtlCommand: Command =
    if ( isLinux ) {
      Command("systemctl")
    } else {
      Command("echo")
    }

  lazy val suopervisorCtlCommand: Command =
    if ( isLinux ) {
      Command("supervisorctl")
    } else {
      Command("echo")
    }

}
