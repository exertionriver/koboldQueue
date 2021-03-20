package action.roles

import ActionConditionsMap
import action.actions.Idle
import action.IAction
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalTime
interface IIdlor : IAction {

    override val actions: ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Idle to null
            )
        )

}

