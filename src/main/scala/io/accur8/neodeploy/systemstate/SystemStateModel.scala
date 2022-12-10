package io.accur8.neodeploy.systemstate


import a8.shared.{CompanionGen, SecretValue, StringValue}
import io.accur8.neodeploy.HealthchecksDotIo
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.systemstate.MxSystemStateModel._
import zio.{&, Task, Trace, ZIO, ZLayer}

object SystemStateModel {

  object SecretContent extends SecretValue.Companion[SecretContent]
  case class SecretContent(value: String) extends SecretValue

  object StateKey extends StringValue.Companion[StateKey] {
    val empty = StateKey("")
  }
  case class StateKey(value: String) extends StringValue

  object UnixPerms extends StringValue.Companion[UnixPerms] {
    val empty = UnixPerms("")
  }
  case class UnixPerms(value: String) extends StringValue

  object PreviousState extends MxPreviousState
  @CompanionGen
  case class PreviousState(resolvedName: String, syncName: SyncName, value: SystemState) {
    lazy val statesByKey = Interpretter.statesByKey(value)
  }

  object SystemStateLogger {
    val simpleLayer =
      ZLayer.succeed(
        new SystemStateLogger {
          override def warn(message: String)(implicit trace: Trace): Task[Unit] =
            ZIO.attemptBlocking(
              wvlet.log.Logger(trace.toString).warn(message)
            )
        }
      )
  }

  trait SystemStateLogger {
    def warn(message: String)(implicit trace: Trace): zio.Task[Unit]
  }

  type Environ = SystemStateLogger & HealthchecksDotIo

  type M[A] = zio.ZIO[Environ, Throwable, A]

}