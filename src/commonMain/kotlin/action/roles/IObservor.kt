package action.roles

import action.*
import action.actions.Look
import action.actions.Reflect
import action.actions.Watch
import kotlin.time.ExperimentalTime

interface IObservor : IAction {

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override val actions : ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Look to null
                , Watch to null
                , Reflect to null
            )
        )
}