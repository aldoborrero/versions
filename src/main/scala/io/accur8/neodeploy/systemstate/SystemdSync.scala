package io.accur8.neodeploy.systemstate


import io.accur8.neodeploy.{Sync, Systemd}
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.Systemd.UnitFile
import io.accur8.neodeploy.model.{Launcher, SystemdDescriptor}
import io.accur8.neodeploy.resolvedmodel.ResolvedApp
import io.accur8.neodeploy.systemstate.SystemStateModel.M
import a8.shared.SharedImports._

case object SystemdSync extends Sync[ResolvedApp] {

  override val name: Sync.SyncName =
    SyncName("systemd")


  override def systemState(input: ResolvedApp): M[SystemState] =
    zsucceed(
      input.descriptor.launcher match {
        case sd: SystemdDescriptor =>
          Systemd.systemState(
            unitName = input.name.value,
            description = input.name.value,
            user = input.user,
            unitFile = UnitFile(
              Type = sd.Type,
              environment = sd.environment.map(t => s"${t._1}=${t._2}").toVector,
              workingDirectory = input.appDirectory.absolutePath,
              execStart = input.descriptor.install.execArgs(input.descriptor, input.appDirectory, input.user.appsRootDirectory).mkString(" "),
            ),
            timerFileOpt = None,
          )
        case _ =>
          SystemState.Empty
      }
    )

}
