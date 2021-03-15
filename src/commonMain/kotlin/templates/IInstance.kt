package templates

import actions.ActionPlex
import actions.IActionPlex
import time.Timer
import kotlin.time.ExperimentalTime

interface IInstance : IActionPlex {
    fun getInstanceName(): String

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    suspend fun perform(registerTimer : Timer, instanceRegister : Register): Timer

    @ExperimentalUnsignedTypes
    override val actionPlex: ActionPlex

    override val moment: Moment

    fun getTemplate() = object {}
}