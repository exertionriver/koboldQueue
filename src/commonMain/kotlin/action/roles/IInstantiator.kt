package action.roles

import ActionConditionsMap
import action.*
import action.actions.Destantiate
import action.actions.Instantiate
import condition.SimpleCondition.Always
import condition.SimpleCondition.Never
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

interface IInstantiator : IAction {

    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override val actions: ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Instantiate to listOf(Never), Destantiate to listOf(Always)
            )
        )
}