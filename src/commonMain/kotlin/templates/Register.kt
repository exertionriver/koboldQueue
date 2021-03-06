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
import condition.SimpleCondition.Always
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import render.RenderActionPlex
import time.Moment
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
class Register (val id : UUID = UUID.randomUUID(), val kInstanceName : String) : IInstance {

    val entries : RegisterEntries = mutableMapOf()

    fun getRegister() : Flow<Register> {
        return flow {
            emit(this@Register)
        }.flowOn(RenderActionPlex.getDispatcher())
    }

    fun getNumKobolds() : Flow<Int> {
        return flow {
            val numKobolds = this@Register.entries.filterKeys { it is Kobold }.keys.toList().size

 //           println ("numKobolds : $numKobolds")

            emit(numKobolds)
        }.flowOn(RenderActionPlex.getDispatcher())
    }

    fun getKobolds() : Flow<List<IInstance>> {
        return flow {
            val kobolds = this@Register.entries.filterKeys { it is Kobold }.keys.toList()

  //          println ("kobolds : $kobolds")

            emit(kobolds)
        }.flowOn(RenderActionPlex.getDispatcher())
    }

    override var actionPlex = ActionPlex(getInstanceId(), getMoment(), getMaxPlexSize())

    override fun getMaxPlexSize(): Int = 16


    fun addInstance(instance : IInstance) { entries.put(instance, startInstance(instance, this)) }

    fun addInstance(kInstanceName : String, instanceTemplate : IInstantiable) { addInstance(instanceTemplate.getInstance(kInstanceName) ) }

    fun startInstance(kInstance : IInstance, register : Register) : Job {

        //add instance to renderer
        RenderActionPlex.addInstance(kInstance)

        //launch instance perform()
        return launch(RenderActionPlex.getCoroutineContext()) { while(true) kInstance.perform(Timer(), register) }
    }

    fun removeInstance(kInstance : IInstance) {
//        println("entries before destantiation: $entries")
//        println("actionPlex before destantiation: ${entries[kInstance.getInstanceName()]!!.actionPlex.stateString()}")

        //remove instance from renderer
        RenderActionPlex.removeInstance(kInstance)

        //cancel all instance actions
        kInstance.actionPlex.cancelAll()

        //cancel instance perform() job
        entries[kInstance]!!.cancel()
//        println("actionPlex after cancel: ${entries[kInstance.getInstanceName()]!!.actionPlex.stateString()}")

        //remove instance from register
        entries.remove(kInstance)

//        println("entries after destantiation: $entries")
    }

    inline fun <reified T : IInstance> getInstance(instanceName : String) : T = entries.filterKeys { it.getInstanceName() == instanceName && entries[it] is T }.values.toList()[0] as T
    inline fun <reified T : IInstance> getInstance(instanceIdx : Int) : T = getInstancesOfType<T>()[instanceIdx]
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IInstance> getInstancesOfType() : List<T> = entries.filterValues { it is T }.values.toList() as List<T>

    override fun toString() = "templates.Register($kInstanceName)"

    override suspend fun perform(timer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        return@coroutineScope timer
    }

    override var interrupted = false

    override fun getMoment() = Moment(momentDuration.toLong())

    override fun getTemplate() = Companion

    override fun getInstanceId() = id

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IInstantiator {

        override fun getTemplateName() : String = Register::class.simpleName!!

        override fun getInstance(kInstanceName: String) = Register(kInstanceName = kInstanceName)

        val momentDuration = 200 //milliseconds

        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(super.actions,
                modMap = mapOf(Instantiate to listOf(Always))
            )
    }
}
