package a8.versions



object RepoAssist {

  def readRepoProperty(propertyName: String): String = {
    import scala.collection.JavaConverters._
    import java.io.FileInputStream
    val props = new java.util.Properties()
    val configFile = new java.io.File(System.getProperty("user.home") + "/.a8/repo.properties")
    if ( configFile.exists() ) {
      val input = new FileInputStream(configFile)
      try {
        props.load(input)
      } finally {
        input.close()
      }
      props.asScala.get(propertyName) match {
        case Some(s) =>
          s
        case None =>
          sys.error(s"could not find property ${propertyName} in ${configFile}")
      }
    } else {
      sys.error(s"config file ${configFile} does not exist")
    }
  }


  def readCredentialsFromUrl(propertyName: String): coursier.credentials.Credentials = {
    import coursier.credentials.Credentials
    val url = new java.net.URL(readRepoProperty(propertyName))
    val args = url.getUserInfo.split(":")
    val user = args(0)
    val password = args(1)
    Credentials(url.getHost, user, password)
  }

}
