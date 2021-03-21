package condition

import ParamList
import action.Action.Companion.ActionNone
import action.ActionPriority.Companion.ActionPriorityNone
import condition.Condition.Companion.ConditionNone
import state.ActionState
import state.ActionState.Companion.ActionStateNone
import time.Timer

class StateCondition(val condition : Condition, val conditionParamList: ParamList? = null) {

    //update constructor
    constructor(copyStateCondition : StateCondition
                , updCondition : Condition = copyStateCondition.condition
                , updConditionParamList : ParamList? = copyStateCondition.conditionParamList
    ) : this (
        condition = updCondition
        , conditionParamList = updConditionParamList
    )

    override fun toString() = "${StateCondition::class.simpleName}($condition, $conditionParamList)"

    companion object {
        val StateConditionNone = StateCondition(ConditionNone)
    }
}