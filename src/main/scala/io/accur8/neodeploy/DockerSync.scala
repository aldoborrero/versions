package io.accur8.neodeploy

import a8.shared.FileSystem.{Directory, File}
import a8.shared.SharedImports._
import a8.shared.ZString
import a8.shared.ZString.ZStringer
import io.accur8.neodeploy.model.{DockerDescriptor, SupervisorDescriptor, SupervisorDirectory}
import io.accur8.neodeploy.resolvedmodel.ResolvedApp
import io.accur8.neodeploy.systemstate.SystemState

object DockerSync extends Sync[ResolvedApp] {

  override val name: Sync.SyncName = Sync.SyncName("docker")

  override def rawSystemState(input: ResolvedApp): SystemState =
    input.descriptor.launcher match {
      case dd: DockerDescriptor =>
        SystemState.RunCommandState(
          installCommands = Vector(
            Overrides.sudoDockerCommand
              .appendArgs("run", "-d", "--name", dd.name)
              .appendArgsSeq(dd.args)
              .asSystemStateCommand,
          ),
          uninstallCommands = Vector(
            Overrides.sudoDockerCommand
              .appendArgs("stop", dd.name)
              .appendArgsSeq(dd.args)
              .asSystemStateCommand,
            Overrides.sudoDockerCommand
              .appendArgs("update", "--restart=no", dd.name)
              .appendArgsSeq(dd.args)
              .asSystemStateCommand,
          ),
        )
      case _ =>
        SystemState.Empty
    }

}
