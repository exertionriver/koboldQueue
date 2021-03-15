package templates

import actions.*
import actions.actionables.ActionConditionsMap
import actions.actionables.IInstantiable
import actions.actionables.IInstantiator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import templates.*
import time.Timer
import actions.actionables.IInstantiator.Companion.Instantiate
import conditions.ISimpleConditionable
import conditions.Probability
import conditions.ProbabilitySelect
import actions.actionables.IInstantiator.Companion.instantiateParamList
import actions.actionables.IObservable
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class Cave(val kInstanceName: String) : IInstance, IObservable {

    @ExperimentalCoroutinesApi
    override suspend fun perform(registerTimer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        if (registerTimer.getMillisecondsElapsed() % moment.milliseconds == 0L) {

            //todo : another list for actions that take two slots
            if (actionPlex.slotsInUse() < maxPlexSize) {

                val extendedAction = ProbabilitySelect<Action>(mapOf(
                    Instantiate to Probability(100,0)
                )).getSelectedProbability()!!

                val actionParamList = when (extendedAction) {
                    Instantiate -> Instantiate.instantiateParamList(Kobold, "krakka${Random.nextInt(256)}", instanceRegister)
                    else -> TODO("something else")
                }

                actionPlex.startAction(extendedAction, extendedAction.actionPriority, actionParamList)
                println("extended action started: ${extendedAction.action} by $kInstanceName at $registerTimer" )

            }

//            println(kInstanceName)
            actionPlex.perform(moment, maxPlexSize)
        //    println(actionPlex.state())
        }

        return@coroutineScope registerTimer
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

        val momentDuration = Moment(1000 * 10) //10 seconds

        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(
                super.actions,
                modMap = mapOf(IInstantiator.Instantiate to listOf(ISimpleConditionable.Always))
            )

    }

}