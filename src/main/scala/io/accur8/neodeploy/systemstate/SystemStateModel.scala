package io.accur8.neodeploy.systemstate


import a8.shared.{CompanionGen, StringValue}
import io.accur8.neodeploy.MxSystemState.MxPreviousState

object SystemStateModel {

  object StateKey extends StringValue.Companion[StateKey] {
    val empty = StateKey("")
  }
  case class StateKey(value: String) extends StringValue

  object UnixPerms extends StringValue.Companion[UnixPerms] {
    val empty = UnixPerms("")
  }
  case class UnixPerms(value: String) extends StringValue

  object PreviousState extends MxPreviousState {
    val empty = PreviousState(SystemState.Empty)
  }
  @CompanionGen
  case class PreviousState(value: SystemState) {
    lazy val statesByKey = Interpretter.statesByKey(value)
  }

}