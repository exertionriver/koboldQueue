package templates

import action.*
import templates.*
import com.soywiz.korio.util.UUID
import kotlinx.coroutines.*
import time.Timer
import action.actions.Idle
import action.actions.Look
import action.actions.Reflect
import action.actions.Watch
import action.roles.IInstantiable
import action.roles.IObservable
import com.soywiz.korge.internal.KorgeInternal
import condition.ISimpleCondition.Companion.Always
import condition.Probability
import condition.ProbabilitySelect
import render.RenderActionPlex
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

    @KorgeInternal
    @ExperimentalCoroutinesApi
    override suspend fun perform(timer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        val checkTimer = Timer()

//        if (timer.getMillisecondsElapsed() / moment.milliseconds > momentCounter) {

//            println("Kobold $kInstanceName perform @ ${ DateTime.now() } RT:${timer.getMillisecondsElapsed()}, $momentCounter")

            momentCounter = (timer.getMillisecondsElapsed() / moment.milliseconds).toInt()

            Companion.baseActions.forEach {
                if (!actionPlex.isBaseActionRunning(it.key) ) {
                    when (it.key) {
                        Look -> actionPlex.startAction(it.key, ActionPriority.BaseAction,  Look.LookParamList(this@Kobold, instanceRegister).actionParamList() )
                        Reflect -> actionPlex.startAction(it.key, ActionPriority.BaseAction, Reflect.ReflectParamList(this@Kobold).actionParamList() )
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
                    Look -> Look.LookParamList(this@Kobold, instanceRegister).actionParamList()
                    Watch -> Watch.WatchParamList(this@Kobold, instanceRegister).actionParamList()
                    else -> Idle.IdleParamList(kInstanceName, Probability(3,2).getValue().toInt()).actionParamList()
                }

                actionPlex.startAction(extendedAction, extendedAction.actionPriority, actionParamList)

//                println("extended action started: ${extendedAction.action} by $kInstanceName at $registerTimer" )
            }

        actionPlex = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) { Action.perform(actionPlex, moment, maxPlexSize) }
        RenderActionPlex.render(id, moment, actionPlex.getImMap())
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

    companion object : IInstantiable, IAction {

        override fun getTemplateName() : String = Kobold::class.simpleName!!

        override fun getInstance(kInstanceName: String) = Kobold(kInstanceName = kInstanceName)

        val momentDuration = Probability(800, 100) //milliseconds

        override val actions: ActionConditionsMap
            get() = super.actions

        override val baseActions: ActionConditionsMap
            get() = mapOf(Look to listOf(Always), Reflect to listOf(Always))
    }
}