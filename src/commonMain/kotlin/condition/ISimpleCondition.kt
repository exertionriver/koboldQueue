package condition

import ConditionList
import condition.SimpleCondition.Always
import condition.SimpleCondition.Eq
import condition.SimpleCondition.Gt
import condition.SimpleCondition.Gte
import condition.SimpleCondition.Lt
import condition.SimpleCondition.Lte
import condition.SimpleCondition.Neq
import condition.SimpleCondition.Never
import kotlinx.coroutines.InternalCoroutinesApi

interface ISimpleCondition : ICondition {

    @InternalCoroutinesApi
    override val conditions : ConditionList
        get() = super.conditions.plus(
            listOf(
                Always, Never, Gt, Gte, Lt, Lte, Eq, Neq
            )
        )

}