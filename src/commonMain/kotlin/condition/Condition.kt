package condition

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

typealias ConditionParamList = List<Any>
typealias ConditionParamMap = Map<Condition, ConditionParamList?>

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
suspend fun ConditionParamMap.evaluate() : Boolean =
    this.map{ Condition.Immediate.evaluate(it.key, it.value) }.reduce{ result : Boolean, element -> result.and(element) }
