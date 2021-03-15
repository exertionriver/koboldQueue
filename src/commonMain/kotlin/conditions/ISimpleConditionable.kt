package conditions

import actions.param

interface ISimpleConditionable : IConditionable {

    override val conditions : ConditionList
        get() = super.conditions.plus(
            listOf(
                Always, Never, SimpleGT, SimpleGTE, SimpleLT, SimpleLTE, SimpleEQ
            )
        )

    companion object {
        val Always = Condition(condition = "always", description = "Condition.Always -> true"
            , evaluator = fun( _ : ConditionParamList?) : Boolean = true)
        val Never = Condition(condition = "never", description = "Condition.Always -> false"
            , evaluator = fun( _ : ConditionParamList?) : Boolean = false)

        private fun ConditionParamList.simpleBinaryParamFirst() = this.param<Double>(0)
        private fun ConditionParamList.simpleBinaryParamSecond() = this.param<Double>(1)

        val SimpleGT = Condition(condition = "simpleGreaterThan", description = null.simpleGTDescription()
            , evaluator = fun(simpleGTParams : ConditionParamList?) : Boolean {
                try {
                    return simpleGTParams!!.simpleBinaryParamFirst() > simpleGTParams.simpleBinaryParamSecond()
                } catch(e : Exception) { this.toString() + "exec(${simpleGTParams})" }
                return false
            }
        )
        fun ConditionParamList?.simpleGTDescription() : String = "Condition.SimpleGT -> " +
            ( (this?.simpleBinaryParamFirst() ?: Double::class.simpleName).toString() + " is greater than " ) +
            ( (this?.simpleBinaryParamSecond() ?: Double::class.simpleName).toString() )

        val SimpleGTE = Condition(condition = "simpleGreaterThanEq", description = null.simpleGTEDescription()
            , evaluator = fun(simpleGTEParams : ConditionParamList?) : Boolean {
                try {
                    return simpleGTEParams!!.simpleBinaryParamFirst() >= simpleGTEParams.simpleBinaryParamSecond()
                } catch(e : Exception) { this.toString() + "exec(${simpleGTEParams})" }
                return false
            }
        )
        fun ConditionParamList?.simpleGTEDescription() : String = "Condition.SimpleGTE -> " +
                ( (this?.simpleBinaryParamFirst() ?: Double::class.simpleName).toString() + " is greater than or equal to " ) +
                ( (this?.simpleBinaryParamSecond() ?: Double::class.simpleName).toString() )

        val SimpleLT = Condition(condition = "simpleLessThan", description = null.simpleLTDescription()
            , evaluator = fun(simpleLTParams : ConditionParamList?) : Boolean {
                try {
                    return simpleLTParams!!.simpleBinaryParamFirst() < simpleLTParams.simpleBinaryParamSecond()
                } catch(e : Exception) { this.toString() + "exec(${simpleLTParams})" }
                return false
            }
        )
        fun ConditionParamList?.simpleLTDescription() : String = "Condition.SimpleLT -> " +
                ( (this?.simpleBinaryParamFirst() ?: Double::class.simpleName).toString() + " is less than " ) +
                ( (this?.simpleBinaryParamSecond() ?: Double::class.simpleName).toString() )

        val SimpleLTE = Condition(condition = "simpleLessThanEq", description = null.simpleLTEDescription()
            , evaluator = fun(simpleLTEParams : ConditionParamList?) : Boolean {
                try {
                    return simpleLTEParams!!.simpleBinaryParamFirst() <= simpleLTEParams.simpleBinaryParamSecond()
                } catch(e : Exception) { this.toString() + "exec(${simpleLTEParams})" }
                return false
            }
        )
        fun ConditionParamList?.simpleLTEDescription() : String = "Condition.SimpleLTE -> " +
                ( (this?.simpleBinaryParamFirst() ?: Double::class.simpleName).toString() + " is less than or equal to " ) +
                ( (this?.simpleBinaryParamSecond() ?: Double::class.simpleName).toString() )

        val SimpleEQ = Condition(condition = "simpleEquals", description = null.simpleEQDescription()
            , evaluator = fun(simpleEQParams : ConditionParamList?) : Boolean {
                try {
                    return simpleEQParams!!.simpleBinaryParamFirst() > simpleEQParams.simpleBinaryParamSecond()
                } catch(e : Exception) { this.toString() + "exec(${simpleEQParams})" }
                return false
            }
        )
        fun ConditionParamList?.simpleEQDescription() : String = "Condition.SimpleEQ -> " +
                ( (this?.simpleBinaryParamFirst() ?: Double::class.simpleName).toString() + " is equal to" ) +
                ( (this?.simpleBinaryParamSecond() ?: Double::class.simpleName).toString() )

    }
}