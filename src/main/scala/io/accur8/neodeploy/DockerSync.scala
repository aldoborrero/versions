package io.accur8.neodeploy

import a8.shared.ZFileSystem.{Directory, File}
import a8.shared.SharedImports._
import a8.shared.ZString
import a8.shared.ZString.ZStringer
import io.accur8.neodeploy.model.{DockerDescriptor, SupervisorDescriptor, SupervisorDirectory}
import io.accur8.neodeploy.resolvedmodel.ResolvedApp
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel.M

object DockerSync extends Sync[ResolvedApp] {

  override val name: Sync.SyncName = Sync.SyncName("docker")


  override def systemState(input: ResolvedApp): M[SystemState] =
    zsucceed(rawSystemState(input))

  def rawSystemState(input: ResolvedApp): SystemState =
    input.descriptor.launcher match {
      case dd: DockerDescriptor =>
        SystemState.DockerState(dd)
      case _ =>
        SystemState.Empty
    }

}
