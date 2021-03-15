package conditions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

interface IConditionable {

    val conditions: ConditionList
        get() = listOf()

    //call this to evaluate condition
    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun evaluate(condition: Condition, conditionParamList : ConditionParamList? = null) : Boolean {

        val evalReturn = condition.evaluator(conditionParamList)

//        AsyncTimer.perceptionChannel.send("eval return @ ${ Clock.System.now().toLocalDateTime(TimeZone.UTC) } ${condition.description}: $evalReturn")

        return evalReturn
    }
}
typealias ConditionList = List<Condition>
