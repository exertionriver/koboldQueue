package templates

import time.GlobalTimer
import time.Timer
import actions.actionables.ActionConditionsMap
import actions.ActionPlex
import actions.actionables.IInstantiable
import actions.actionables.IInstantiator
import com.soywiz.korio.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import actions.actionables.IInstantiator.Companion.Instantiate
import com.soywiz.korio.async.runBlockingNoJs
import conditions.ISimpleConditionable.Companion.Always
import kotlin.time.ExperimentalTime

open class Register (val id : UUID = UUID.randomUUID(), val kInstanceName : String) : IInstance {

    val entries = mutableMapOf<String, IInstance>()

    @ExperimentalUnsignedTypes
    override val actionPlex: ActionPlex = mutableMapOf()

    override val maxPlexSize: Int = 16

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun addInstance(instance : IInstance) { entries.put(instance.getInstanceName(), instance); startInstance(instance, this) }
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun addInstance(kInstanceName : String, instanceTemplate : IInstantiable) { addInstance(instanceTemplate.getInstance(kInstanceName) ) }
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun startInstance(instance : IInstance, register : Register) = runBlockingNoJs {
        GlobalTimer.globalChannel.send("instantiated ${instance.getInstanceName()} in ${register.kInstanceName} ")

        return@runBlockingNoJs
    }

    fun removeInstance(kInstanceName : String) = entries.remove(entries.filterKeys { it == kInstanceName }.keys.toList()[0])

    inline fun <reified T : IInstance> getInstance(instanceName : String) : T = entries.filterKeys { it == instanceName && entries[it] is T }.values.toList()[0] as T
    inline fun <reified T : IInstance> getInstance(instanceIdx : Int) : T = getInstancesOfType<T>()[instanceIdx]
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IInstance> getInstancesOfType() : List<T> = entries.filterValues { it is T }.values.toList() as List<T>

    override fun toString() = "templates.Register($kInstanceName)"

    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override suspend fun perform(registerTimer : Timer, instanceRegister : Register) : Timer {

        coroutineScope {
            for (instance in instanceRegister.entries.values) {
                launch {
                    instance.perform(registerTimer, instanceRegister)
                }
            }

        return@coroutineScope
        }

        return registerTimer
    }

    override val moment = momentDuration

    override fun getTemplate() = Companion

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IInstantiator {

        override val templateName : String = Register::class.simpleName!!

        override fun getInstance(kInstanceName: String) = Register(kInstanceName = kInstanceName)

        val momentDuration = Moment.Immediate //milliseconds

        @ExperimentalUnsignedTypes
        @ExperimentalTime
        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(super.actions,
                modMap = mapOf(Instantiate to listOf(Always))
            )
    }
}