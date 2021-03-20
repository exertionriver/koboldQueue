package action

import ConditionParamMap
import ParamList
import action.actions.Idle
import com.soywiz.korio.util.UUID
import condition.SimpleCondition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import state.ActionState
import templates.Moment
import time.Timer
import kotlin.time.ExperimentalTime

class ActionPlex(val instanceID : UUID, val moment : Moment, val maxPlexSize : Int) {

    @ExperimentalUnsignedTypes
    val entries : MutableMap<UUID, StateAction> = mutableMapOf() //slots to StateActions, max of maxPlexSize

    @ExperimentalUnsignedTypes
    fun getEntriesDisplaySortedMap() = entries.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.actionState }
        .thenByDescending { it.second.timer.getMillisecondsElapsed() }).toMap()

    @ExperimentalUnsignedTypes
    fun getEntriesPerformSortedMap() = entries.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() })

    @ExperimentalUnsignedTypes
    fun slotsInUse() : Int {

        val inProcessActions = entries.filterValues { plexAction -> ActionState.InProcess.contains(plexAction.actionState) }

        return if (inProcessActions.isNullOrEmpty()) 0 else inProcessActions.map{ plexAction -> plexAction.value.plexSlotsFilled }.reduce{ slotsInUse : Int, plexActionSlotsFilled -> slotsInUse + plexActionSlotsFilled }
    }

    @ExperimentalUnsignedTypes
    fun slotsAvailable() : Int {

        return maxPlexSize - slotsInUse()
    }

    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun momentsPassed(stateActionUuid : UUID) : Int = (getActionTimer(stateActionUuid).getMillisecondsElapsed() / moment.milliseconds).toInt()

    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun isActionQueued(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionQueue)

    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun isActionPrepared(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionPrepare) &&
            (momentsPassed(stateActionUuid) >= entries[stateActionUuid]!!.action.momentsToPrepare)

    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun isActionExecuted(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionExecute) &&
            (momentsPassed(stateActionUuid) >= entries[stateActionUuid]!!.action.momentsToExecute)

    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun isActionRecovered(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionRecover) &&
            (momentsPassed(stateActionUuid) >= entries[stateActionUuid]!!.action.momentsToRecover)

    @ExperimentalUnsignedTypes
    fun isBaseActionRunning(action: Action) : Boolean =
        entries.filterValues { stateAction ->  stateAction.action == action && stateAction.actionPriority == ActionPriority.BaseAction }.isNotEmpty()

    @ExperimentalUnsignedTypes
    fun getStateAction(stateActionUuid: UUID) : StateAction = if (entries[stateActionUuid] != null) entries[stateActionUuid]!! else StateAction.StateActionNone

    @ExperimentalUnsignedTypes
    fun getAction(stateActionUuid: UUID) : Action = if (entries[stateActionUuid] != null) entries[stateActionUuid]!!.action else Action.ActionNone

    @ExperimentalUnsignedTypes
    fun getActionType(stateActionUuid: UUID) : ActionType = if (entries[stateActionUuid] != null) getAction(stateActionUuid).actionType else ActionType.OneTimeExec

    @ExperimentalUnsignedTypes
    fun getActionState(stateActionUuid: UUID) : ActionState = if (entries[stateActionUuid] != null) entries[stateActionUuid]!!.actionState else ActionState.ActionStateNone

    @ExperimentalUnsignedTypes
    fun getActionPriority(stateActionUuid: UUID) : ActionPriority = if (entries[stateActionUuid] != null) entries[stateActionUuid]!!.actionPriority else ActionPriority.ActionPriorityNone

    @ExperimentalUnsignedTypes
    fun getActionParamList(stateActionUuid: UUID) : ParamList? = if (entries[stateActionUuid] != null) entries[stateActionUuid]!!.actionParamList else null

    @ExperimentalUnsignedTypes
    fun getActionTimer(stateActionUuid: UUID) : Timer = if (entries[stateActionUuid] != null) entries[stateActionUuid]!!.timer else Timer()

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun cycleState(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid) ) {
            ActionState.ActionQueue -> entries[stateActionUuid] = StateAction(copyStateAction = entries[stateActionUuid]!!, updActionState = ActionState.ActionPrepare, updTimer = Timer())
            ActionState.ActionPrepare -> entries[stateActionUuid] = StateAction(copyStateAction = entries[stateActionUuid]!!, updActionState = ActionState.ActionExecute, updTimer = Timer())
            ActionState.ActionExecute -> entries[stateActionUuid] = StateAction(copyStateAction = entries[stateActionUuid]!!, updActionState = ActionState.ActionRecover, updTimer = Timer())
            ActionState.ActionRecover -> entries[stateActionUuid] = StateAction(copyStateAction = entries[stateActionUuid]!!, updActionState = ActionState.ActionQueue, updTimer = Timer())
            else -> entries[stateActionUuid] = StateAction(copyStateAction = entries[stateActionUuid]!!, updActionState = ActionState.ActionQueue)
        }
    }

    @ExperimentalUnsignedTypes
    fun initAction(action: Action, actionPriority: ActionPriority, actionParamList : ParamList? = null) {
        val newPlexActionUuid = UUID.randomUUID()
        when (actionPriority) {
            ActionPriority.BaseAction -> entries[newPlexActionUuid] = StateAction(Action(copyAction = action, updActionType = ActionType.Continual), action.plexSlotsRequired,
                ActionState.ActionQueue, actionPriority, actionParamList)
            else -> entries[newPlexActionUuid] = StateAction(action, action.plexSlotsRequired, ActionState.ActionQueue, actionPriority, actionParamList)
        }

    }

    @ExperimentalUnsignedTypes
    fun cancelAction(stateActionUuid: UUID) = entries.remove(stateActionUuid)

    @ExperimentalUnsignedTypes
    fun cancelAll() = entries.clear()

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun queueAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionRecover -> if (getActionType(stateActionUuid) == ActionType.Continual) cycleState(stateActionUuid) else cancelAction(stateActionUuid)
            else -> cycleState(stateActionUuid)
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun prepareAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionQueue -> {
                if ( preempt(stateActionUuid) ) cycleState(stateActionUuid)
                else if ( getActionPriority(stateActionUuid) == ActionPriority.BaseAction && interrupt(entries[stateActionUuid]!!.action.plexSlotsRequired) ) cycleState(stateActionUuid)
            }
            else -> cycleState(stateActionUuid)
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    suspend fun executeAction(stateActionUuid : UUID, conditionParamMap: ConditionParamMap = mapOf(SimpleCondition.Always to null)) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionPrepare -> {
                cycleState(stateActionUuid)

                Action.Immediate.execute(getAction(stateActionUuid), conditionParamMap, getActionParamList(stateActionUuid))

                //extend execution time by idle moments param
                if ( getAction(stateActionUuid) == Idle) {
                    entries[stateActionUuid] = StateAction(copyStateAction = getStateAction(stateActionUuid)
                        , updAction = Action(getAction(stateActionUuid), updMomentsToExecute = Idle.IdleParamList(getActionParamList(stateActionUuid)!!).moments!! ) )
                }

            }
            else -> cycleState(stateActionUuid)
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun recoverAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            else -> cycleState(stateActionUuid) //cycle state by default
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun interruptAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionExecute -> queueAction(stateActionUuid) //assess if there is penalty
            ActionState.ActionRecover -> entries[stateActionUuid] = StateAction(copyStateAction = getStateAction(stateActionUuid), updActionState = ActionState.ActionRecover, updTimer = Timer())    //restart recover
            else -> queueAction(stateActionUuid) //no penalty
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun preemptAction(stateActionUuid : UUID) {
        entries[stateActionUuid] = StateAction(copyStateAction = getStateAction(stateActionUuid), updActionState = ActionState.ActionQueue, updTimer = Timer())
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun interrupt(slotsToInterrupt : Int) : Boolean {

//    println("interrupt($slotsToInterrupt, $maxPlexSize)")

        var filledSlotsToInterrupt = slotsToInterrupt - slotsAvailable()

        if (filledSlotsToInterrupt <= 0 ) return true

        val interruptables = entries.filterValues { ActionState.Interruptable.contains(it.actionState) }.toList().sortedWith (compareByDescending <Pair<UUID, StateAction>> { it.second.actionPriority }.thenBy { it.second.timer.getMillisecondsElapsed() })

//    println("interruptables(${interruptables.size}):")

//    interruptables.forEach { println("interruptable: $it") }

        val interruptableFilledSlots = if (interruptables.isNullOrEmpty()) 0 else interruptables.map { it.second.plexSlotsFilled }.reduce{ result : Int, element -> result + element }

        if (filledSlotsToInterrupt <= interruptableFilledSlots) {
            for (interruptable in interruptables) {
                if (filledSlotsToInterrupt <= 0) return true

//            println("${interruptable.first} INTERRUPTED!")
                interruptAction(interruptable.first)
                filledSlotsToInterrupt -= entries[interruptable.first]!!.plexSlotsFilled
            }
        }
        return false
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun preempt(stateActionUuid: UUID) : Boolean {

//    println("preempt($actionPriority, $slotsToPreempt, $maxPlexSize)")

        var filledSlotsToPreempt = getStateAction(stateActionUuid).action.plexSlotsRequired - slotsAvailable()

    println("filled slots to preempt:${filledSlotsToPreempt}")

        if (filledSlotsToPreempt <= 0) return true

        val preemptables = entries.filterValues {
            ActionState.Preemptable.contains(it.actionState) && it.actionPriority > getActionPriority(stateActionUuid)
        }.toList().sortedWith (compareByDescending <Pair<UUID, StateAction>> { it.second.actionPriority }.thenBy { it.second.timer.getMillisecondsElapsed() })

    println("preemptables(${preemptables.size}):")

    preemptables.forEach { println("preemptable: $it") }

        val preemptableFilledSlots = if (preemptables.isNullOrEmpty()) 0 else preemptables.map { it.second.plexSlotsFilled }.reduce{ result : Int, element -> result + element }

        if (filledSlotsToPreempt <= preemptableFilledSlots) {
            for (preemptable in preemptables) {
                if (filledSlotsToPreempt <= 0) return true

            println("${preemptable.first} PREEMPTED!" )
                preemptAction(preemptable.first)
                filledSlotsToPreempt -= entries[preemptable.first]!!.plexSlotsFilled
            }
        }

        return false
    }

    @ExperimentalUnsignedTypes
    fun stateString() : List<String> {

        val returnState = mutableListOf<String>()

        returnState.add("slots in use: ${slotsInUse()}")

        entries.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() }).forEach{ returnState.add("${it.first}: ${it.second}") }

        return returnState
    }

    companion object {
        @ExperimentalCoroutinesApi
        @ExperimentalTime
        @ExperimentalUnsignedTypes
        suspend fun perform(actionPlex: ActionPlex) : ActionPlex = coroutineScope {

            //          val checkTimer = Timer()

            actionPlex.getEntriesPerformSortedMap().forEach{
                //        println(it.first)
                when {
                    actionPlex.isActionQueued(it.first) -> actionPlex.prepareAction(it.first)
                    actionPlex.isActionPrepared(it.first) -> actionPlex.executeAction(it.first)
                    actionPlex.isActionExecuted(it.first) -> actionPlex.recoverAction(it.first)
                    actionPlex.isActionRecovered(it.first) -> actionPlex.queueAction(it.first)
                }
            }

//            println("ActionPlex checktimer: ${checkTimer.getMillisecondsElapsed()}")

            return@coroutineScope actionPlex
        }

    }
}