package actions.actionables

import actions.Action
import actions.ActionParamList
import com.soywiz.klock.DateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import time.GlobalTimer
import conditions.ConditionList
import conditions.ConditionParamMap
import conditions.ISimpleConditionable
import conditions.ISimpleConditionable.Companion.Always
import conditions.evaluate
import kotlin.time.ExperimentalTime

interface IActionable : ISimpleConditionable {
    //used to associate actions with conditions for templates

    @ExperimentalTime
    val actions: ActionConditionsMap
        get() = mapOf()

    @ExperimentalTime
    val baseActions: ActionConditionsMap
        get() = mapOf()

    @ExperimentalTime
    fun getActionConditionList(action: Action): ConditionList = actions[action] ?: listOf(Always)

    fun modOrSrcXorMap(srcMap: ActionConditionsMap, modMap: ActionConditionsMap): ActionConditionsMap =
        modMap.plus(srcMap.filterKeys { !modMap.keys.contains(it) })

    //call this to execute action
    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun execute(action: Action, conditionParamMap : ConditionParamMap = mapOf(Always to null), actionParamList : ActionParamList? = null) = coroutineScope {

        //evaluate conditionParameters if they exist; if these evaluate to true, run action
        if ( conditionParamMap.filterKeys { getActionConditionList(action).contains(it) }.evaluate() ) {

            GlobalTimer.perceptionChannel.send("exec return @ ${ DateTime.now() } ${action.executor(actionParamList)}")
        }

        return@coroutineScope
    }
}

typealias ActionConditionsMap = Map<Action, ConditionList?>

