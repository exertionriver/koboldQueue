package condition

import ConditionDescription
import ConditionEvaluator
import ParamList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

open class Condition(val condition : String, val description : ConditionDescription, val evaluator : ConditionEvaluator) {

    //update constructor
    constructor(copyCondition : Condition
                , updCondition : String = copyCondition.condition
                , updDescription : ConditionDescription = copyCondition.description
                , updEvaluator : ConditionEvaluator = copyCondition.evaluator
    ) : this (
        condition = updCondition
        , description = updDescription
        , evaluator = updEvaluator
    )

    object Immediate : ICondition {
        @ExperimentalCoroutinesApi
        @ExperimentalTime
        @ExperimentalUnsignedTypes
        override suspend fun evaluate(condition: Condition, conditionParamList : ParamList?) : Boolean? {
            return super.evaluate(condition, conditionParamList)
        }
    }

    override fun toString() = "${Condition::class.simpleName}($condition, $description, evaluator())"

    override fun equals(other: Any?): Boolean {
        return this.condition == (other as Condition).condition
    }

    override fun hashCode(): Int {
        var result = condition.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + evaluator.hashCode()
        return result
    }

    companion object {
        val ConditionNone = Condition(condition = "none", description = fun() : String = "none", evaluator = fun(_: ParamList?) : Boolean = false)

    }
}
