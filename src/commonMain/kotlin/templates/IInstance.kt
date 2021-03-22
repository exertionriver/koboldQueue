package templates

import action.ActionPlex
import action.IActionPlex
import com.soywiz.korio.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import time.Timer
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
interface IInstance : IActionPlex {

    fun getInstanceId(): UUID

    fun getInstanceName(): String

    var interrupted : Boolean

    suspend fun perform(timer : Timer, instanceRegister : Register): Timer

    override var actionPlex: ActionPlex

    fun getTemplate() = object {}
}