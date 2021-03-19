package condition

import ConditionParamList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

open class Condition(val condition : String, val description : String, val evaluator : (conditionParams : ConditionParamList?) -> Boolean) {

    object Immediate : ICondition {
        @ExperimentalCoroutinesApi
        @ExperimentalTime
        @ExperimentalUnsignedTypes
        override suspend fun evaluate(condition: Condition, conditionParamList : ConditionParamList?) : Boolean {
            return super.evaluate(condition, conditionParamList)
        }
    }
}
