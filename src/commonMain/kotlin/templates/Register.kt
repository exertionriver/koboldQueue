package templates

import ActionConditionsMap
import time.Timer
import RegisterEntries
import action.ActionPlex
import action.actions.Instantiate
import action.roles.IInstantiable
import action.roles.IInstantiator
import com.soywiz.korio.util.UUID
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoSuspensions
import condition.SimpleCondition.Always
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import registerChannel
import render.RenderActionPlex
import kotlin.time.ExperimentalTime

class Register (val id : UUID = UUID.randomUUID(), val kInstanceName : String) : IInstance {

    val entries : RegisterEntries = mutableMapOf()

 //   fun getEntries() = entries.toMap()

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    fun getNumKobolds() : Flow<Int> {
        return flow {
            val numKobolds = this@Register.entries.filterKeys { it is Kobold }.keys.toList().size

 //           println ("numKobolds : $numKobolds")

            emit(numKobolds)
        }.flowOn(Dispatchers.Default)
    }

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    fun getKobolds() : Flow<List<IInstance>> {
        return flow {
            val kobolds = this@Register.entries.filterKeys { it is Kobold }.keys.toList()

  //          println ("kobolds : $kobolds")

            emit(kobolds)
        }.flowOn(Dispatchers.Default)
    }

    @ExperimentalUnsignedTypes
    override var actionPlex = ActionPlex(getInstanceId(), getMoment(), getMaxPlexSize())

    override fun getMaxPlexSize(): Int = 16

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun addInstance(instance : IInstance) { entries.put(instance, startInstance(instance, this)) }
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun addInstance(kInstanceName : String, instanceTemplate : IInstantiable) { addInstance(instanceTemplate.getInstance(kInstanceName) ) }
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun startInstance(instance : IInstance, register : Register) : Job {

        RenderActionPlex.instances.put(RenderActionPlex.getOpenPosition(), instance)

        launch(CoroutineScope(Dispatchers.Default).coroutineContext) { registerChannel.send(Pair(register.id, register.entries.toMap())) }

        return launch(CoroutineScope(Dispatchers.Default).coroutineContext) { while(true) instance.perform(Timer(), register) }
    }


    @ExperimentalUnsignedTypes
    fun removeInstance(kInstance : IInstance, register : Register) = runBlockingNoSuspensions {
//        GlobalTimer.globalChannel.send("destantiated ${kInstance.getInstanceName()} in ${register.kInstanceName} ")
//        println("entries before destantiation: $entries")
//        println("actionPlex before destantiation: ${entries[kInstance.getInstanceName()]!!.actionPlex.stateString()}")

        //remove from rendering
        RenderActionPlex.removeInstance(kInstance)

        //cancel all actions
        kInstance.actionPlex.cancelAll()

        //cancel perform job
        entries[kInstance]!!.cancel()
//        println("actionPlex after cancel: ${entries[kInstance.getInstanceName()]!!.actionPlex.stateString()}")

        //remove from register
        entries.remove(kInstance)

        launch(CoroutineScope(Dispatchers.Default).coroutineContext) { registerChannel.send(Pair(register.id, register.entries.toMap())) }

//        iterator() ; while (removeIter.hasNext()) if (removeIter.next().key == kInstanceName) removeIter.remove()
//        println("entries after destantiation: $entries")
//        val removeRenderIter = RenderActionPlex.instances.iterator() ; while (removeRenderIter.hasNext()) if (removeRenderIter.next().value == kInstance) removeIter.remove()
    }

    inline fun <reified T : IInstance> getInstance(instanceName : String) : T = entries.filterKeys { it.getInstanceName() == instanceName && entries[it] is T }.values.toList()[0] as T
    inline fun <reified T : IInstance> getInstance(instanceIdx : Int) : T = getInstancesOfType<T>()[instanceIdx]
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IInstance> getInstancesOfType() : List<T> = entries.filterValues { it is T }.values.toList() as List<T>

    override fun toString() = "templates.Register($kInstanceName)"

    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override suspend fun perform(registerTimer : Timer, instanceRegister : Register) : Timer = coroutineScope {
/*
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
        //delay(mSecRenderDelay - registerTimer.getMillisecondsElapsed())
 */       return@coroutineScope registerTimer
    }

    override fun getMoment() = Moment(momentDuration.toLong())

    override fun getTemplate() = Companion

    override fun getInstanceId() = id

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IInstantiator {

        override fun getTemplateName() : String = Register::class.simpleName!!

        override fun getInstance(kInstanceName: String) = Register(kInstanceName = kInstanceName)

        val momentDuration = 200 //Moment.Immediate //milliseconds

        @ExperimentalCoroutinesApi
        @ExperimentalUnsignedTypes
        @ExperimentalTime
        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(super.actions,
                modMap = mapOf(Instantiate to listOf(Always))
            )
    }
}
