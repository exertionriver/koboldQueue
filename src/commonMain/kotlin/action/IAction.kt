package action

import ActionConditionsMap
import ConditionList
import ConditionParamMap
import ParamList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import condition.ISimpleCondition
import condition.SimpleCondition.Always
import kotlin.time.ExperimentalTime

interface IAction : ISimpleCondition {
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
    suspend fun execute(action: Action, conditionParamMap : ConditionParamMap = mapOf(Always to null), actionParamList : ParamList? = null) = coroutineScope {

        //evaluate conditionParameters if they exist; if these evaluate to true, run action
        if (conditionParamMap.filterKeys { getActionConditionList(action).contains(it) }.evaluate() == true) {

            action.executor(actionParamList)

 //           GlobalChannel.logInfoChannel.send("exec return @ ${ DateTime.now() } ${action.executor(actionParamList)}")
        }

        return@coroutineScope
    }
}