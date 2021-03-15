package templates

import actions.*
import actions.actionables.ActionConditionsMap
import actions.actionables.IActionable
import templates.*
import actions.actionables.IObservor.Companion.Look
import actions.actionables.IObservor.Companion.Watch
import actions.actionables.IIdlor.Companion.Idle
import com.soywiz.korio.util.UUID
import kotlinx.coroutines.*
import time.Timer
import actions.actionables.IObservor.Companion.Reflect
import actions.actionables.IObservor.Companion.lookParamList
import actions.actionables.IObservor.Companion.reflectParamList
import actions.actionables.IObservor.Companion.watchParamList
import conditions.ISimpleConditionable.Companion.Always
import conditions.Probability
import conditions.ProbabilitySelect
import actions.actionables.IIdlor.Companion.idleParamList
import actions.actionables.IInstantiable
import actions.actionables.IObservable
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class Kobold(val id : UUID = UUID.randomUUID(), val kInstanceName : String) : IInstance, IObservable {

    override fun getDescription(): String = ProbabilitySelect(mapOf(
        "ugly Kobold!" to Probability(40)
        ,"toothy Kobold!" to Probability(30)
        ,"scaly Kobold!" to Probability(30)
    )).getSelectedProbability()!!

    @ExperimentalCoroutinesApi
    override suspend fun perform(registerTimer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        if (registerTimer.getMillisecondsElapsed() % moment.milliseconds == 0L) {

            Companion.baseActions.forEach {
                if (!actionPlex.isBaseActionRunning(it.key) ) {
                    when (it.key) {
                        Look -> actionPlex.startAction(it.key, ActionPriority.BaseAction,  Look.lookParamList(kInstanceName, instanceRegister) )
                        Reflect -> actionPlex.startAction(it.key, ActionPriority.BaseAction, Reflect.reflectParamList(kInstanceName) )
                        else -> actionPlex.startAction(it.key, ActionPriority.BaseAction)
                    }
                    println("base action started: ${it.key.action} by $kInstanceName at $registerTimer" )
                }
            }

            //todo : another list for actions that take two slots
            if (actionPlex.slotsInUse() < maxPlexSize) {

                val extendedAction = ProbabilitySelect<Action>(mapOf(
                    Idle to Probability(70, 0)
                    , Look to Probability(15,0)
                    , Watch to Probability(15,0)
                )).getSelectedProbability()!!

                val actionParamList = when (extendedAction) {
                    Look -> Look.lookParamList(kInstanceName, instanceRegister)
                    Watch -> Watch.watchParamList(kInstanceName, instanceRegister)
                    else -> Idle.idleParamList(kInstanceName, Probability(3,2).getValue().toInt())
                }

                actionPlex.startAction(extendedAction, extendedAction.actionPriority, actionParamList)

                println("extended action started: ${extendedAction.action} by $kInstanceName at $registerTimer" )
            }

//            println(kInstanceName)
            actionPlex.perform(moment, maxPlexSize)
//            println(actionPlex.state())
        }

        return@coroutineScope registerTimer
    }

    override val actionPlex: ActionPlex = mutableMapOf()

    override val maxPlexSize: Int = 5

    override val moment = Moment(momentDuration.getValue().toLong())

    override fun getTemplate() = Companion

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IActionable {

        override val templateName : String = Kobold::class.simpleName!!

        override fun getInstance(kInstanceName: String) = Kobold(kInstanceName = kInstanceName)

        val momentDuration = Probability(800, 100) //milliseconds

        override val actions: ActionConditionsMap
            get() = super.actions

        override val baseActions: ActionConditionsMap
            get() = mapOf(Look to listOf(Always), Reflect to listOf(Always))
    }
}