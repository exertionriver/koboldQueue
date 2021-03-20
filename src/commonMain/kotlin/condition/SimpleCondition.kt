package condition

import ParamList
import param

object SimpleCondition {

    val Always = Condition(condition = "always", description = fun() = "${SimpleCondition::class.simpleName} -> Checking always == true"
        , evaluator = fun( _ : ParamList?) : Boolean = true)
    val Never = Condition(condition = "never", description = fun() = "${SimpleCondition::class.simpleName} -> Checking never == false"
        , evaluator = fun( _ : ParamList?) : Boolean = false)

    class BinaryParamList(var first : Comparable<Any>?, var second : Comparable<Any>?, var operator : String?) {

        constructor(conditionParamList: ParamList) : this(
            first = conditionParamList.param<Comparable<Any>>(0)
            , second = conditionParamList.param<Comparable<Any>>(1)
            , operator = conditionParamList.param<String>(2)
        )

        constructor(nullConstructor : Nothing? = null, operator: String? = null) : this(first = null, second = null, operator = operator)

        fun description() : String = "${SimpleCondition::class.simpleName} -> " +
                "Checking ${firstOrT()} ${operatorOrT()} ${secondOrT()}"

        private fun firstOrT() = first ?: Comparable::class.simpleName

        private fun secondOrT() = second ?: Comparable::class.simpleName

        private fun operatorOrT() = operator ?: String::class.simpleName

        fun conditionParamList() = listOf(first, second) as ParamList
    }

    @ExperimentalUnsignedTypes
    fun params(lambda: BinaryParamList.() -> Unit) = BinaryParamList().apply(lambda).conditionParamList()

    val Gt = Condition(condition = "simpleGreaterThan"
        , description = fun () : String = BinaryParamList(operator = ">").description()
        , evaluator = fun (gtParams : ParamList?) : Boolean? {
            if (gtParams == null) return null

            return BinaryParamList(gtParams).second?.let { BinaryParamList(gtParams).first?.compareTo(it) }!! > 0
        }
    )

    val Gte = Condition(condition = "simpleGreaterThanEq"
        , description = fun () : String = BinaryParamList(operator = ">=").description()
        , evaluator = fun(gteParams : ParamList?) : Boolean? {
            if (gteParams == null) return null

            return BinaryParamList(gteParams).second?.let { BinaryParamList(gteParams).first?.compareTo(it) }!! >= 0
        }
    )

    val Lt = Condition(condition = "simpleLessThan"
        , description = fun () : String = BinaryParamList(operator = "<").description()
        , evaluator = fun (ltParams : ParamList?) : Boolean? {
            if (ltParams == null) return null

            return BinaryParamList(ltParams).second?.let { BinaryParamList(ltParams).first?.compareTo(it) }!! < 0
        }
    )

    val Lte = Condition(condition = "simpleLessThanEq"
        , description = fun () : String = BinaryParamList(operator = "<=").description()
        , evaluator = fun (lteParams : ParamList?) : Boolean? {
            if (lteParams == null) return null

            return BinaryParamList(lteParams).second?.let { BinaryParamList(lteParams).first?.compareTo(it) }!! <= 0
        }
    )

    val Eq = Condition(condition = "simpleEq"
        , description = fun () : String = BinaryParamList(operator = "==").description()
        , evaluator = fun (eqParams : ParamList?) : Boolean? {
            if (eqParams == null) return null

            return BinaryParamList(eqParams).second?.let { BinaryParamList(eqParams).first?.compareTo(it) }!! == 0
        }
    )

}