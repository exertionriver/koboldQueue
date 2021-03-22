package action.roles

import ActionConditionsMap
import action.*
import action.actions.Look
import action.actions.Reflect
import action.actions.Watch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
interface IObservor : IAction {

    override val actions : ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Look to null
                , Watch to null
                , Reflect to null
            )
        )
}