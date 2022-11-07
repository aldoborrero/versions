package io.accur8.neodeploy


import a8.shared.FileSystem.file
import zio.Task
import a8.shared.SharedImports._
import a8.shared.app.Logging
import a8.shared.{FileSystem, ZString}
import com.softwaremill.sttp.Uri
import io.accur8.neodeploy.model.{AuthorizedKey, PersonnelId, UserDescriptor}
import io.accur8.neodeploy.resolvedmodel.ResolvedUser

object AuthorizedKeys2Sync extends ConfigFileSync[ResolvedUser] with Logging {

  override val name: Sync.SyncName = Sync.SyncName("authorized_keys2")

  override def configFile(input: ResolvedUser): FileSystem.File =
    input.home.subdir(".ssh").file("authorized_keys2")

  override def configFileContents(input: ResolvedUser): Task[Option[String]] = {

    val personnelKeys =
      input
        .personnel
        .flatMap(_.resolvedKeys)

    zsucceed(
      (personnelKeys ++ input.authorizedKeys)
        .map(_.value)
        .mkString("\n")
        .some
    )
  }


}

