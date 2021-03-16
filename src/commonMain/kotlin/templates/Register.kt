package templates

import time.GlobalTimer
import time.Timer
import actions.actionables.ActionConditionsMap
import actions.ActionPlex
import actions.actionables.IInstantiable
import actions.actionables.IInstantiator
import com.soywiz.korio.util.UUID
import actions.actionables.IInstantiator.Companion.Instantiate
import actions.cancelAction
import actions.cancelAll
import actions.stateString
import com.soywiz.klock.DateTime
import conditions.ISimpleConditionable.Companion.Always
import kotlinx.coroutines.*
import render.RenderActionPlex
import time.GlobalTimer.mSecPerceptionDelay
import time.GlobalTimer.mSecRenderDelay
import kotlin.time.ExperimentalTime

open class Register (val id : UUID = UUID.randomUUID(), val kInstanceName : String) : IInstance {

    val entries = mutableMapOf<String, IInstance>()

 //   fun getEntries() = entries.toMap()

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
    fun startInstance(instance : IInstance, register : Register) {

        //GlobalTimer.globalChannel.send("instantiated ${instance.getInstanceName()} in ${register.kInstanceName} ")
        RenderActionPlex.instances.put(RenderActionPlex.getOpenPosition(), instance)
//        instance.actionPlex.render()
    }

    @ExperimentalUnsignedTypes
    fun removeInstance(kInstance : IInstance, register : Register) {
//        GlobalTimer.globalChannel.send("destantiated ${kInstance.getInstanceName()} in ${register.kInstanceName} ")
        println("entries before destantiation: $entries")
        println("actionPlex before destantiation: ${entries[kInstance.getInstanceName()]!!.actionPlex.stateString()}")
        entries[kInstance.getInstanceName()]!!.actionPlex.cancelAll()
        println("actionPlex after cancel: ${entries[kInstance.getInstanceName()]!!.actionPlex.stateString()}")
        entries.remove(kInstance.getInstanceName())
//        iterator() ; while (removeIter.hasNext()) if (removeIter.next().key == kInstanceName) removeIter.remove()
        println("entries after destantiation: $entries")
//        val removeRenderIter = RenderActionPlex.instances.iterator() ; while (removeRenderIter.hasNext()) if (removeRenderIter.next().value == kInstance) removeIter.remove()
        RenderActionPlex.removeInstance(kInstance)
    }

    inline fun <reified T : IInstance> getInstance(instanceName : String) : T = entries.filterKeys { it == instanceName && entries[it] is T }.values.toList()[0] as T
    inline fun <reified T : IInstance> getInstance(instanceIdx : Int) : T = getInstancesOfType<T>()[instanceIdx]
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IInstance> getInstancesOfType() : List<T> = entries.filterValues { it is T }.values.toList() as List<T>

    override fun toString() = "templates.Register($kInstanceName)"

    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override suspend fun perform(registerTimer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        if (registerTimer.getMillisecondsElapsed() >= mSecRenderDelay) {
            println("register @ ${ DateTime.now() } GT:${registerTimer.getMillisecondsElapsed()}")

            coroutineScope {
                for (entry in entries.toMap()) {
                    var instanceTimer = entry.value.perform(registerTimer, instanceRegister)
                    launch {
                        instanceTimer = entry.value.perform(instanceTimer, instanceRegister)
                    }
                }
            }

            return@coroutineScope Timer()
        }
        delay(mSecRenderDelay - registerTimer.getMillisecondsElapsed())
        return@coroutineScope registerTimer
    }

    override val moment = momentDuration

    override fun getTemplate() = Companion

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IInstantiator {

        override val templateName : String = Register::class.simpleName!!

        override fun getInstance(kInstanceName: String) = Register(kInstanceName = kInstanceName)

        val momentDuration = Moment.Immediate //milliseconds

        @ExperimentalCoroutinesApi
        @ExperimentalUnsignedTypes
        @ExperimentalTime
        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(super.actions,
                modMap = mapOf(Instantiate to listOf(Always))
            )
    }
}