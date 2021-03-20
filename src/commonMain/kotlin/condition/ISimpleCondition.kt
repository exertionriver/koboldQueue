package condition

import ConditionList
import condition.SimpleCondition.Always
import condition.SimpleCondition.Eq
import condition.SimpleCondition.Gt
import condition.SimpleCondition.Gte
import condition.SimpleCondition.Lt
import condition.SimpleCondition.Lte
import condition.SimpleCondition.Never


interface ISimpleCondition : ICondition {

    override val conditions : ConditionList
        get() = super.conditions.plus(
            listOf(
                Always, Never, Gt, Gte, Lt, Lte, Eq
            )
        )

}