package actions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import actions.ActionPriority.Companion.BaseAction
import state.ActionState.Companion.ActionExecute
import state.ActionState.Companion.ActionNotFound
import state.ActionState.Companion.ActionPrepare
import state.ActionState.Companion.ActionQueue
import state.ActionState.Companion.ActionRecover
import state.ActionState.Companion.InProcess
import state.ActionState.Companion.Interruptable
import state.ActionState.Companion.Preemptable
import actions.ActionType.Companion.Continual
import com.soywiz.korio.util.UUID
import templates.Moment
import time.Timer
import actions.actionables.IIdlor.Companion.Idle
import conditions.ConditionParamMap
import conditions.ISimpleConditionable.Companion.Always
import state.ActionState
import state.StateAction
import actions.actionables.IIdlor.Companion.idleParamMoments
import com.soywiz.klock.DateTime
import com.soywiz.korio.async.runBlockingNoJs
import kotlinx.coroutines.coroutineScope
import time.GlobalTimer
import kotlin.time.ExperimentalTime

interface IActionPlex {

    @ExperimentalUnsignedTypes
    val actionPlex : ActionPlex

    val moment : Moment

    val maxPlexSize : Int
}

@ExperimentalUnsignedTypes
typealias ActionPlex = MutableMap<UUID, StateAction> //slots to StateActions, max of maxPlexSize

@ExperimentalUnsignedTypes
fun ActionPlex.slotsInUse() : Int {

    val inProcessActions = this.filterValues { InProcess.contains(it.actionState) }

    val plexSlotsInUse = if (inProcessActions.isNullOrEmpty()) 0 else inProcessActions.map{ it.value.plexSlotsFilled }.reduce{ result : Int, element -> result + element }

    return plexSlotsInUse
}

@ExperimentalUnsignedTypes
fun ActionPlex.slotsAvailable(maxPlexSize : Int) : Int {

    return maxPlexSize - this.slotsInUse()
}

@ExperimentalUnsignedTypes
fun ActionPlex.getActionState(uuid: UUID) : ActionState = if (this[uuid] != null) this[uuid]!!.actionState else ActionNotFound

@ExperimentalUnsignedTypes
fun ActionPlex.getActionTimer(uuid : UUID) : Timer = if (this[uuid] != null) this[uuid]!!.timer else Timer()

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.cycleState(uuid : UUID) {
    when (this.getActionState(uuid) ) {
        ActionQueue -> this[uuid] = StateAction(copyStateAction = this[uuid]!!, updActionState = ActionPrepare)
        ActionPrepare -> this[uuid] = StateAction(copyStateAction = this[uuid]!!, updActionState = ActionExecute, updTimer = Timer())
        ActionExecute -> this[uuid] = StateAction(copyStateAction = this[uuid]!!, updActionState = ActionRecover, updTimer = Timer())
        ActionRecover -> this[uuid] = StateAction(copyStateAction = this[uuid]!!, updActionState = ActionQueue, updTimer = Timer())
        else -> this[uuid] = StateAction(copyStateAction = this[uuid]!!, updActionState = ActionQueue, updTimer = Timer())
    }
}

@ExperimentalUnsignedTypes
fun ActionPlex.startAction(action: Action, actionPriority: ActionPriority, actionParamList : ActionParamList? = null) {
    val actionUuid = UUID.randomUUID()
    when (actionPriority) {
        BaseAction -> this[actionUuid] = StateAction(Action(copyAction = action, updActionType = Continual), action.plexSlotsRequired, ActionQueue, actionPriority, actionParamList)
        else -> this[actionUuid] = StateAction(action, action.plexSlotsRequired, ActionQueue, actionPriority, actionParamList)
    }

}

@ExperimentalUnsignedTypes
fun ActionPlex.isBaseActionRunning(action: Action) : Boolean =
    this.filterValues { it.action.action == action.action && it.actionPriority == BaseAction }.isNotEmpty()

@ExperimentalUnsignedTypes
fun ActionPlex.cancelAction(uuid: UUID) = this.remove(uuid)

@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.checkMomentsPassed(uuid : UUID, moment : Moment) : Int = (getActionTimer(uuid).getMillisecondsElapsed() / moment.milliseconds).toInt()

@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.isActionQueued(uuid : UUID, moment : Moment) =
    (getActionState(uuid) == ActionQueue)

@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.isActionPrepared(uuid : UUID, moment : Moment) =
    (getActionState(uuid) == ActionPrepare) && (checkMomentsPassed(uuid, moment) >= this[uuid]!!.action.momentsToPrepare)

@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.isActionExecuted(uuid : UUID, moment : Moment) =
    (getActionState(uuid) == ActionExecute) && (checkMomentsPassed(uuid, moment) >= this[uuid]!!.action.momentsToExecute)

@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.isActionRecovered(uuid : UUID, moment : Moment) =
    (getActionState(uuid) == ActionRecover) && (checkMomentsPassed(uuid, moment) >= this[uuid]!!.action.momentsToRecover)

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.queueAction(uuid : UUID) {
    when (getActionState(uuid)) {
        ActionRecover -> if (this[uuid]!!.action.actionType == Continual) this.cycleState(uuid) else this.cancelAction(uuid)
        else -> this.cycleState(uuid)
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.prepareAction(uuid : UUID, maxPlexSize: Int) {
    when (getActionState(uuid)) {
        ActionQueue -> {
            if ( this.preempt(uuid, maxPlexSize) ) this.cycleState(uuid)
            else if ( ( this[uuid]!!.actionPriority == BaseAction) && this.interrupt(this[uuid]!!.action.plexSlotsRequired, maxPlexSize) ) this.cycleState(uuid)
        }
        else -> this.cycleState(uuid)
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun ActionPlex.executeAction(uuid : UUID, conditionParamMap: ConditionParamMap = mapOf(Always to null)) {
    when (getActionState(uuid)) {
        ActionPrepare -> {
            this.cycleState(uuid)

            Action.Immediate.execute(this[uuid]!!.action, conditionParamMap, this[uuid]!!.actionParamList)

            //extend execution time by idle moments param
            if ( this[uuid]!!.action == Idle) this[uuid] = StateAction(copyStateAction = this[uuid]!!, updAction = Action(Idle, updMomentsToExecute = this[uuid]!!.actionParamList!!.idleParamMoments()))

        }
        else -> this.cycleState(uuid)
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.recoverAction(uuid : UUID) {
    when (getActionState(uuid)) {
        else -> this.cycleState(uuid) //cycle state by default
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.interruptAction(uuid : UUID) {
    when (getActionState(uuid)) {
        ActionExecute -> this.queueAction(uuid) //assess if there is penalty
        ActionRecover -> this[uuid] = StateAction(copyStateAction = this[uuid]!!, updActionState = ActionRecover, updTimer = Timer())    //restart recover
        else -> this.queueAction(uuid) //no penalty
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.preemptAction(uuid : UUID) {
    this[uuid] = StateAction(copyStateAction = this[uuid]!!, updActionState = ActionQueue, updTimer = Timer())
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.interrupt(slotsToInterrupt : Int, maxPlexSize: Int) : Boolean {

//    println("interrupt($slotsToInterrupt, $maxPlexSize)")

    var filledSlotsToInterrupt = slotsToInterrupt - this.slotsAvailable(maxPlexSize)

    if (filledSlotsToInterrupt <= 0 ) return true

    val interruptables = this.filterValues { Interruptable.contains(it.actionState) }.toList().sortedWith (compareByDescending <Pair<UUID, StateAction>> { it.second.actionPriority }.thenBy { it.second.timer.getMillisecondsElapsed() })

//    println("interruptables(${interruptables.size}):")

//    interruptables.forEach { println("interruptable: $it") }

    val interruptableFilledSlots = if (interruptables.isNullOrEmpty()) 0 else interruptables.map { it.second.plexSlotsFilled }.reduce{ result : Int, element -> result + element }

    if (filledSlotsToInterrupt <= interruptableFilledSlots) {
        for (interruptable in interruptables) {
            if (filledSlotsToInterrupt <= 0) return true

//            println("${interruptable.first} INTERRUPTED!")
            this.interruptAction(interruptable.first)
            filledSlotsToInterrupt -= this[interruptable.first]!!.plexSlotsFilled
        }
    }
    return false
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
fun ActionPlex.preempt(uuid: UUID, maxPlexSize: Int) : Boolean {

//    println("preempt($actionPriority, $slotsToPreempt, $maxPlexSize)")

    var filledSlotsToPreempt = this[uuid]!!.action.plexSlotsRequired - this.slotsAvailable(maxPlexSize)

//    println("filled slots to preempt:${filledSlotsToPreempt}")

    if (filledSlotsToPreempt <= 0) return true

    val preemptables = this.filterValues {
        Preemptable.contains(it.actionState) && it.actionPriority > this[uuid]!!.actionPriority
    }.toList().sortedWith (compareByDescending <Pair<UUID, StateAction>> { it.second.actionPriority }.thenBy { it.second.timer.getMillisecondsElapsed() })

//    println("preemptables(${preemptables.size}):")

//    preemptables.forEach { println("preemptable: $it") }

    val preemptableFilledSlots = if (preemptables.isNullOrEmpty()) 0 else preemptables.map { it.second.plexSlotsFilled }.reduce{ result : Int, element -> result + element }

    if (filledSlotsToPreempt <= preemptableFilledSlots) {
        for (preemptable in preemptables) {
            if (filledSlotsToPreempt <= 0) return true

//            println("${preemptable.first} PREEMPTED!" )
            this.preemptAction(preemptable.first)
            filledSlotsToPreempt -= this[preemptable.first]!!.plexSlotsFilled
        }
    }

    return false
}

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun ActionPlex.perform(moment : Moment, maxPlexSize: Int) {

    this.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() }).forEach{
//        println(it.first)
        when {
            isActionQueued(it.first, moment) -> this.prepareAction(it.first, maxPlexSize)
            isActionPrepared(it.first, moment) -> this.executeAction(it.first)
            isActionExecuted(it.first, moment) -> this.recoverAction(it.first)
            isActionRecovered(it.first, moment) -> this.queueAction(it.first)
        }
    }
}

@ExperimentalUnsignedTypes
suspend fun ActionPlex.state() : List<String> {

    val returnState = mutableListOf<String>()

    returnState.add("slots in use: ${slotsInUse()}")

    this.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() }).forEach{ returnState.add("${it.first}: ${it.second}") }

    return returnState
}

@ExperimentalUnsignedTypes
suspend fun ActionPlex.render() {
    GlobalTimer.renderChannel.send(this)
}