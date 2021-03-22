package action

import ActionConditionsMap
import ConditionList
import ConditionParamMap
import ParamList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import condition.ISimpleCondition
import condition.SimpleCondition.Always
import render.RenderActionPlex
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface IAction : ISimpleCondition {
    //used to associate actions with conditions for templates

    val actions: ActionConditionsMap
        get() = mapOf()

    val baseActions: ActionConditionsMap
        get() = mapOf()

    fun getActionConditionList(action: Action): ConditionList = actions[action] ?: listOf(Always)

    fun modOrSrcXorMap(srcMap: ActionConditionsMap, modMap: ActionConditionsMap): ActionConditionsMap =
        modMap.plus(srcMap.filterKeys { !modMap.keys.contains(it) })

    //call this to execute action
    suspend fun execute(action: Action, conditionParamMap : ConditionParamMap = mapOf(Always to null), actionParamList : ParamList? = null) = coroutineScope {

        //evaluate conditionParameters if they exist; if these evaluate to true, run action
//        if (conditionParamMap.filterKeys { getActionConditionList(action).contains(it) }.evaluate() == true) {
        if (conditionParamMap.evaluate() == true) {

            val description = action.executor(actionParamList)

            RenderActionPlex.renderDescription(description!!)

   //         println(description!!)

        }

        return@coroutineScope
    }
}
