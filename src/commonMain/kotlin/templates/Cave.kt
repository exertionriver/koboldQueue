package templates

import action.*
import action.ActionConditionsMap
import action.actions.Destantiate
import action.actions.Instantiate
import action.roles.IInstantiable
import action.roles.IInstantiator
import templates.*
import time.Timer
import condition.ISimpleCondition
import condition.Probability
import condition.ProbabilitySelect
import action.roles.IObservable
import com.soywiz.korio.util.UUID
import kotlinx.coroutines.*
import render.RenderActionPlex
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class Cave(private val id : UUID = UUID.randomUUID(), private val kInstanceName: String) : IInstance, IObservable {

    var momentCounter = 0

    lateinit var entries : RegisterEntries

    @ExperimentalCoroutinesApi
    override suspend fun perform(timer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        val checkTimer = Timer()

   //     if (timer.getMillisecondsElapsed() / moment.milliseconds > momentCounter) {

//            println("Cave $kInstanceName perform @ ${ DateTime.now() } RT:${timer.getMillisecondsElapsed()} $momentCounter")

            while (!registerChannel.isEmpty) {
                val regData = registerChannel.receive()

                if (regData.first == instanceRegister.id) entries = regData.second.toMutableMap()
            }

            momentCounter = (timer.getMillisecondsElapsed() / moment.milliseconds).toInt()

            if (momentCounter % 5 == 0) {

            //todo : another list for actions that take two slots
            if (actionPlex.slotsInUse() < maxPlexSize) {

                val koboldInstances = entries.filterKeys { it is Kobold }.keys.toList() as List<Kobold>

                val extendedAction = if (koboldInstances.isNotEmpty())
                    if (koboldInstances.size > 8)
                        ProbabilitySelect(mapOf(
                            Instantiate to Probability(0,0)
                            , Destantiate to Probability(100,0)
                        )).getSelectedProbability()!!
                    else
                        ProbabilitySelect(mapOf(
                            Instantiate to Probability(80,0)
                            , Destantiate to Probability(20,0)
                        )).getSelectedProbability()!!
                else
                    ProbabilitySelect(mapOf(
                        Instantiate to Probability(100,0)
                        , Destantiate to Probability(0,0)
                    )).getSelectedProbability()!!

                val actionParamList = when (extendedAction) {
                    Instantiate -> Instantiate.InstantiateParamList(Kobold, "krakka${Random.nextInt(256)}", instanceRegister).actionParamList()
                    Destantiate -> Destantiate.DestantiateParamList(koboldInstances[Random.nextInt(koboldInstances.size)], instanceRegister).actionParamList()
                    else -> TODO("something else")
                }

                actionPlex.startAction(extendedAction, extendedAction.actionPriority, actionParamList)
//                println("extended action started: ${extendedAction.action} by $kInstanceName at $timer" )
            }

                actionPlex = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) { Action.perform(actionPlex, moment, maxPlexSize) }
                RenderActionPlex.render(id, moment, actionPlex.getImMap())
                delay(moment.milliseconds - checkTimer.getMillisecondsElapsed())

                println("Cave $kInstanceName checktimer after: ${checkTimer.getMillisecondsElapsed()} ${moment.milliseconds}")


      //      println("Cave $kInstanceName checktimer: ${checkTimer.getMillisecondsElapsed()} $momentCounter")
            }

//        delay(GlobalTimer.mSecRenderDelay)

            return@coroutineScope Timer()

 //       } //else

  //      println("Kobold $kInstanceName checktimer: ${checkTimer.getMillisecondsElapsed()} $momentCounter")

   //     return@coroutineScope timer
    }

    override fun getDescription(): String = "spooky cave!"

    override var actionPlex: ActionPlex = mutableMapOf()

    override val maxPlexSize: Int = 1

    override val moment = momentDuration

    override fun getTemplate() = Companion

    override fun getInstanceId() = id

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IInstantiator {

        override fun getInstance(kInstanceName: String) = Cave(kInstanceName = kInstanceName)

        override val templateName : String = Cave::class.simpleName!!

        val momentDuration = Moment(500*4)

        @ExperimentalCoroutinesApi
        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(
                super.actions,
                modMap = mapOf(Instantiate to listOf(ISimpleCondition.Always))
            )

    }

}