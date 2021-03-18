package templates

import action.ActionPlex
import action.IActionPlex
import com.soywiz.korio.util.UUID
import time.Timer
import kotlin.time.ExperimentalTime

interface IInstance : IActionPlex {

    fun getInstanceId(): UUID

    fun getInstanceName(): String

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    suspend fun perform(registerTimer : Timer, instanceRegister : Register): Timer

    @ExperimentalUnsignedTypes
    override val actionPlex: ActionPlex

    override val moment: Moment

    fun getTemplate() = object {}
}