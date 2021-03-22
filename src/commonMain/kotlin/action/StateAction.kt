package action

import ParamList
import action.Action.Companion.ActionNone
import action.ActionPriority.Companion.ActionPriorityNone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import state.ActionState
import state.ActionState.Companion.ActionStateNone
import time.Timer
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
class StateAction (val action : Action, val plexSlotsFilled : Int = 0, val actionState: ActionState = ActionStateNone, val actionPriority: ActionPriority = ActionPriorityNone, val actionParamList: ParamList? = null, val timer: Timer = Timer()) {

    //update constructor
    constructor(copyStateAction : StateAction
                , updAction : Action = copyStateAction.action
                , updPlexSlotsFilled : Int = copyStateAction.plexSlotsFilled
                , updActionState : ActionState = copyStateAction.actionState
                , updActionPriority : ActionPriority = copyStateAction.actionPriority
                , updActionParamList : ParamList? = copyStateAction.actionParamList
                , updTimer : Timer = copyStateAction.timer
    ) : this (
        action = updAction
        , plexSlotsFilled = updPlexSlotsFilled
        , actionState = updActionState
        , actionPriority = updActionPriority
        , actionParamList = updActionParamList
        , timer = updTimer
    )

    override fun toString() = "${StateAction::class.simpleName}($action, $plexSlotsFilled, $actionState, $actionPriority, $actionParamList, $timer)"

    companion object {

        val StateActionNone = StateAction(ActionNone)
    }
}