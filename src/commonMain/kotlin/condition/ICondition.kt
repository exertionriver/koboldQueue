package condition

import ConditionList
import ConditionParamList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

interface ICondition {

    val conditions: ConditionList
        get() = listOf()

    //call this to evaluate condition
    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun evaluate(condition: Condition, conditionParamList : ConditionParamList? = null) : Boolean {

        val evalReturn = condition.evaluator(conditionParamList)

   //     GlobalChannel.logInfoChannel.send("eval return @ ${ DateTime.now() } ${condition.description}: $evalReturn")

        return evalReturn
    }
}
