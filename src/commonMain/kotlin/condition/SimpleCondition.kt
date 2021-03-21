package condition

import ParamList
import com.soywiz.korio.async.*
import fparam
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import param
import templates.IInstance
import time.Timer

object SimpleCondition {

    val Always = Condition(conditionLabel = "always", description = fun() = "${SimpleCondition::class.simpleName} -> Checking always == true"
        , evaluator = fun( _ : ParamList?) : Boolean = true)
    val Never = Condition(conditionLabel = "never", description = fun() = "${SimpleCondition::class.simpleName} -> Checking never == false"
        , evaluator = fun( _ : ParamList?) : Boolean = false)

    class BinaryParamList(var first : Comparable<Any>?, var second : Comparable<Any>?, var operator : String? = null) {

        constructor(conditionParamList: ParamList) : this(
            first = conditionParamList.fparam<Comparable<Any>>(0)
            , second = conditionParamList.fparam<Comparable<Any>>(1)
        )

        constructor(nullConstructor : Nothing? = null, operator: String? = null) : this(first = null, second = null, operator = operator)

        fun description() : String = "${SimpleCondition::class.simpleName} -> " +
                "Checking ${firstOrT()} ${operatorOrT()} ${secondOrT()}"

        private fun firstOrT() = first ?: Comparable::class.simpleName

        private fun secondOrT() = second ?: Comparable::class.simpleName

        private fun operatorOrT() = operator ?: String::class.simpleName

        fun conditionParamList() = listOf(first, second) as ParamList
    }

    class BinaryFlowParamList(var first : Flow<Any>?, var second : Flow<Any>?, var operator : String? = null) {

        constructor(nullConstructor : Nothing? = null, operator: String? = null) : this(first = null, second = null, operator = operator)

        fun conditionParamList() = listOf(first, second) as ParamList
    }

    @ExperimentalUnsignedTypes
    fun params(lambda: BinaryParamList.() -> Unit) = BinaryParamList().apply(lambda).conditionParamList()

    @ExperimentalUnsignedTypes
    fun fparams(lambda: BinaryFlowParamList.() -> Unit) = BinaryFlowParamList().apply(lambda).conditionParamList()

    val Gt = Condition(conditionLabel = "simpleGreaterThan"
        , description = fun () : String = BinaryParamList(operator = ">").description()
        , evaluator = fun (gtParams : ParamList?) : Boolean? {
            if (gtParams == null) return null

            return BinaryParamList(gtParams).second?.let { BinaryParamList(gtParams).first?.compareTo(it) }!! > 0
        }
    )

    val Gte = Condition(conditionLabel = "simpleGreaterThanEq"
        , description = fun () : String = BinaryParamList(operator = ">=").description()
        , evaluator = fun(gteParams : ParamList?) : Boolean? {
            if (gteParams == null) return null

            return BinaryParamList(gteParams).second?.let { BinaryParamList(gteParams).first?.compareTo(it) }!! >= 0
        }
    )

    val Lt = Condition(conditionLabel = "simpleLessThan"
        , description = fun () : String = BinaryParamList(operator = "<").description()
        , evaluator = fun (ltParams : ParamList?) : Boolean? {
            if (ltParams == null) return null

            return BinaryParamList(ltParams).second?.let { BinaryParamList(ltParams).first?.compareTo(it) }!! < 0
        }
    )

    val Lte = Condition(conditionLabel = "simpleLessThanEq"
        , description = fun () : String = BinaryParamList(operator = "<=").description()
        , evaluator = fun (lteParams : ParamList?) : Boolean? {
            if (lteParams == null) return null

            return BinaryParamList(lteParams).second?.let { BinaryParamList(lteParams).first?.compareTo(it) }!! <= 0
        }
    )

    val Eq = Condition(conditionLabel = "simpleEq"
        , description = fun () : String = BinaryParamList(operator = "==").description()
        , evaluator = fun (eqParams : ParamList?) : Boolean? {
            if (eqParams == null) return null

            return BinaryParamList(eqParams).second?.let { BinaryParamList(eqParams).first?.compareTo(it) }!! == 0
        }
    )

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    val FlowEq = Condition(conditionLabel = "simpleFlowEq"
        , description = fun () : String = BinaryParamList(operator = "==").description()
        , evaluator = fun (eqParams : ParamList?) : Boolean {
            if (eqParams == null) return false

            val checkTimer = Timer()
            val firstVar = BinaryParamList(eqParams).first
            println ("firstVar: $firstVar, ${checkTimer.getMillisecondsElapsed()}")

            val secondVar = BinaryParamList(eqParams).second
            println ("secondVar: $secondVar, ${checkTimer.getMillisecondsElapsed()}")

            println (firstVar == secondVar)

            return firstVar == secondVar
        }
    )


    val Neq = Condition(conditionLabel = "simpleNotEq"
        , description = fun () : String = BinaryParamList(operator = "!=").description()
        , evaluator = fun (eqParams : ParamList?) : Boolean? {
            if (eqParams == null) return null

            return BinaryParamList(eqParams).second?.let { BinaryParamList(eqParams).first?.compareTo(it) }!! != 0
        }
    )

}