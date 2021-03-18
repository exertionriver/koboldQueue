package action.roles

import action.*
import action.actions.Destantiate
import action.actions.Instantiate
import condition.ISimpleCondition.Companion.Always
import condition.ISimpleCondition.Companion.Never
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
