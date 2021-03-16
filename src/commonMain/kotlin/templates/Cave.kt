package templates

import actions.*
import actions.actionables.ActionConditionsMap
import actions.actionables.IInstantiable
import actions.actionables.IInstantiator
import actions.actionables.IInstantiator.Companion.Destantiate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import templates.*
import time.Timer
import actions.actionables.IInstantiator.Companion.Instantiate
import actions.actionables.IInstantiator.Companion.destantiateParamList
import conditions.ISimpleConditionable
import conditions.Probability
import conditions.ProbabilitySelect
import actions.actionables.IInstantiator.Companion.instantiateParamList
import actions.actionables.IObservable
import com.soywiz.klock.DateTime
import kotlinx.coroutines.delay
import time.GlobalTimer
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class Cave(val kInstanceName: String) : IInstance, IObservable {

    var momentCounter = 0

    @ExperimentalCoroutinesApi
    override suspend fun perform(timer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        val checkTimer = Timer()

        if (timer.getMillisecondsElapsed() / moment.milliseconds > momentCounter) {

            println("Cave $kInstanceName perform @ ${ DateTime.now() } RT:${timer.getMillisecondsElapsed()} $momentCounter")

            momentCounter = (timer.getMillisecondsElapsed() / moment.milliseconds).toInt()

            //todo : another list for actions that take two slots
            if (actionPlex.slotsInUse() < maxPlexSize) {

                val koboldInstances = instanceRegister.getInstancesOfType<Kobold>()

                val extendedAction = if (koboldInstances.isNotEmpty())
                    if (koboldInstances.size > 2)
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
                    Instantiate -> Instantiate.instantiateParamList(Kobold, "krakka${Random.nextInt(256)}", instanceRegister)
                    Destantiate -> Destantiate.destantiateParamList(koboldInstances[Random.nextInt(koboldInstances.size)], instanceRegister)
                    else -> TODO("something else")
                }

                actionPlex.startAction(extendedAction, extendedAction.actionPriority, actionParamList)
//                println("extended action started: ${extendedAction.action} by $kInstanceName at $timer" )
            }

            actionPlex.perform(moment, maxPlexSize)

            println("Cave $kInstanceName checktimer: ${checkTimer.getMillisecondsElapsed()} $momentCounter")

            return@coroutineScope Timer()

        } else delay(GlobalTimer.mSecRenderDelay)

        println("Kobold $kInstanceName checktimer: ${checkTimer.getMillisecondsElapsed()} $momentCounter")

        return@coroutineScope timer
    }

    override fun getDescription(): String = "spooky cave!"

    override val actionPlex: ActionPlex = mutableMapOf()

    override val maxPlexSize: Int = 1

    override val moment = momentDuration

    override fun getTemplate() = Companion

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IInstantiator {

        override fun getInstance(kInstanceName: String) = Cave(kInstanceName)

        override val templateName : String = Cave::class.simpleName!!

        val momentDuration = Moment(1000 * 5) //10 seconds

        @ExperimentalCoroutinesApi
        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(
                super.actions,
                modMap = mapOf(Instantiate to listOf(ISimpleConditionable.Always))
            )

    }

}