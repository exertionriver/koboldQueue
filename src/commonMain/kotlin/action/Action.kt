package action

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import action.ActionPriority.Companion.MediumSecond
import action.ActionType.Companion.OneTimeExec
import com.soywiz.korio.util.UUID
import condition.ConditionParamMap
import templates.Moment
import kotlin.time.ExperimentalTime

open class Action(val action : String, val momentsToPrepare : Int = 2, val momentsToExecute : Int = 3, val momentsToRecover : Int = 2, val plexSlotsRequired : Int = 1,
                  val actionType : ActionType = OneTimeExec, val actionPriority : ActionPriority = MediumSecond, val description : ActionDescription, val executor : ActionExecutor
) {

    //update constructor
    constructor(copyAction : Action
                , updAction : String = copyAction.action
                , updMomentsToPrepare : Int = copyAction.momentsToPrepare
                , updMomentsToExecute : Int = copyAction.momentsToExecute
                , updMomentsToRecover : Int = copyAction.momentsToRecover
                , updPlexSlotsRequired: Int = copyAction.plexSlotsRequired
                , updActionType: ActionType = copyAction.actionType
                , updActionPriority: ActionPriority = copyAction.actionPriority
                , updDescription : ActionDescription = copyAction.description
                , updExecutor : ActionExecutor = copyAction.executor
    ) : this (
        action = updAction
        , momentsToPrepare = updMomentsToPrepare
        , momentsToExecute = updMomentsToExecute
        , momentsToRecover = updMomentsToRecover
        , plexSlotsRequired = updPlexSlotsRequired
        , actionType = updActionType
        , actionPriority = updActionPriority
        , description = updDescription
        , executor = updExecutor
    )

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    object Immediate : IAction {

        @ExperimentalCoroutinesApi
        override suspend fun execute(action: Action, conditionParamMap: ConditionParamMap, actionParamList: ActionParamList?) = coroutineScope {
            super.execute(action, conditionParamMap, actionParamList)

            return@coroutineScope
        }

    }

    override fun toString() = "${Action::class.simpleName}($action, $momentsToPrepare, $momentsToExecute, $momentsToRecover, $plexSlotsRequired, $actionType, $actionPriority, $description, executor())"

    companion object {

        @ExperimentalCoroutinesApi
        @ExperimentalTime
        @ExperimentalUnsignedTypes
        suspend fun perform(actionPlex : ActionPlex, moment : Moment, maxPlexSize: Int) : ActionPlex = coroutineScope {

  //          val checkTimer = Timer()

            actionPlex.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() }).forEach{
    //        println(it.first)
                when {
                    actionPlex.isActionQueued(it.first, moment) -> actionPlex.prepareAction(it.first, maxPlexSize)
                    actionPlex.isActionPrepared(it.first, moment) -> actionPlex.executeAction(it.first)
                    actionPlex.isActionExecuted(it.first, moment) -> actionPlex.recoverAction(it.first)
                    actionPlex.isActionRecovered(it.first, moment) -> actionPlex.queueAction(it.first)
                }
            }

//            println("ActionPlex checktimer: ${checkTimer.getMillisecondsElapsed()}")

            return@coroutineScope actionPlex
        }
    }
}



typealias ActionParamList = List<Any>
inline fun <reified T> ActionParamList.param(index : Int) : T = if (this[index] is T) this[index] as T else throw IllegalArgumentException(this.toString())

typealias ActionDescription = () -> String
typealias ActionExecutor = (actionParams : ActionParamList?) -> String?

//typealias ActionExecutor = (actionParams : Any?) -> String?
