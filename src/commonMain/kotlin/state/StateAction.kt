package state

import actions.Action
import actions.ActionParamList
import actions.ActionPriority
import time.Timer

class StateAction(val action : Action, val plexSlotsFilled : Int, val actionState: ActionState, val actionPriority: ActionPriority, val actionParamList: ActionParamList? = null, val timer: Timer = Timer()) {

    //update constructor
    constructor(copyStateAction : StateAction
                , updAction : Action = copyStateAction.action
                , updPlexSlotsFilled : Int = copyStateAction.plexSlotsFilled
                , updActionState : ActionState = copyStateAction.actionState
                , updActionPriority : ActionPriority = copyStateAction.actionPriority
                , updActionParamList : ActionParamList? = copyStateAction.actionParamList
                , updTimer : Timer = copyStateAction.timer
    ) : this (
        action = updAction
        , plexSlotsFilled = updPlexSlotsFilled
        , actionState = updActionState
        , actionPriority = updActionPriority
        , actionParamList = updActionParamList
        , timer = updTimer
    )

    override fun toString() = "${StateAction::class.simpleName}($action, $actionState, $actionPriority, $actionParamList, $timer)"
}