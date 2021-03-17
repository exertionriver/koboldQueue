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
import com.soywiz.klock.DateTime
import render.RenderActionPlex
import time.GlobalTimer
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class Kobold(private val id : UUID = UUID.randomUUID(), private val kInstanceName : String) : IInstance, IObservable {

    override fun getDescription(): String = ProbabilitySelect(mapOf(
        "ugly Kobold!" to Probability(40)
        ,"toothy Kobold!" to Probability(30)
        ,"scaly Kobold!" to Probability(30)
    )).getSelectedProbability()!!


    var momentCounter = 0

    @ExperimentalCoroutinesApi
    override suspend fun perform(timer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        val checkTimer = Timer()

//        if (timer.getMillisecondsElapsed() / moment.milliseconds > momentCounter) {

//            println("Kobold $kInstanceName perform @ ${ DateTime.now() } RT:${timer.getMillisecondsElapsed()}, $momentCounter")

            momentCounter = (timer.getMillisecondsElapsed() / moment.milliseconds).toInt()

            Companion.baseActions.forEach {
                if (!actionPlex.isBaseActionRunning(it.key) ) {
                    when (it.key) {
                        Look -> actionPlex.startAction(it.key, ActionPriority.BaseAction,  Look.lookParamList(kInstanceName, instanceRegister) )
                        Reflect -> actionPlex.startAction(it.key, ActionPriority.BaseAction, Reflect.reflectParamList(kInstanceName) )
                        else -> actionPlex.startAction(it.key, ActionPriority.BaseAction)
                    }
//                    println("base action started: ${it.key.action} by $kInstanceName at $registerTimer" )
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

//                println("extended action started: ${extendedAction.action} by $kInstanceName at $registerTimer" )
            }

        actionPlex = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) { Action.perform(actionPlex, moment, maxPlexSize) }
        RenderActionPlex.render(id, actionPlex.toMap())
//        println("Kobold $kInstanceName checktimer before: ${checkTimer.getMillisecondsElapsed()} $momentCounter")
        delay(moment.milliseconds - checkTimer.getMillisecondsElapsed())

        println("Kobold $kInstanceName checktimer after: ${checkTimer.getMillisecondsElapsed()} ${moment.milliseconds}")

//        delay(GlobalTimer.mSecRenderDelay)

        return@coroutineScope Timer()
     //   } //else delay(GlobalTimer.mSecRenderDelay)

     //   return@coroutineScope timer
    }

    override var actionPlex: ActionPlex = mutableMapOf()

    override val maxPlexSize: Int = 5

    override val moment = Moment(momentDuration.getValue().toLong())

    override fun getTemplate() = Companion

    override fun getInstanceId() = id

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