package actions.actionables

import actions.Action
import actions.ActionParamList
import actions.ActionPriority
import actions.ActionPriority.Companion.LowThird
import actions.param
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalTime
interface IIdlor : IActionable {

    override val actions : ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Idle to null
            )
        )

    fun getDescription() : String

    companion object {
        val Idle = Action(action = "idle"
            , actionPriority = LowThird
            , description = null.idleDescription()
            , executor = fun (idleParams : ActionParamList?) : String? {
                try {
                    return idleParams.idleDescription()
                } catch(e : Exception) { this.toString() + "exec(${idleParams})" }
                return null
            }
        )
        fun Action.idleParamList(kInstanceName : String, moments : Int) = listOf(kInstanceName, moments)
        fun ActionParamList.idleParamInstanceName() = this.param<String>(0)
        fun ActionParamList.idleParamMoments() = this.param<Int>(1)
        fun ActionParamList?.idleDescription() : String = "Action.Idle -> " +
            ("IInstance named " + (this?.idleParamInstanceName() ?: String::class.simpleName ) ) +
            (" putters around for " + (this?.idleParamMoments() ?: Double::class.simpleName ) + " moments")

    }

}