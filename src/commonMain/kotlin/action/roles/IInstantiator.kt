package action.roles

import ActionConditionsMap
import action.*
import action.actions.Destantiate
import action.actions.Instantiate
import condition.SimpleCondition.Always
import condition.SimpleCondition.Never
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface IInstantiator : IAction {

    override val actions: ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Instantiate to listOf(Never), Destantiate to listOf(Always)
            )
        )
}
