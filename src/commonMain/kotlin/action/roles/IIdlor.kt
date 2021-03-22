package action.roles

import ActionConditionsMap
import action.actions.Idle
import action.IAction
import action.actions.Screech
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface IIdlor : IAction {

    override val actions: ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Idle to null
                , Screech to null
            )
        )

}


