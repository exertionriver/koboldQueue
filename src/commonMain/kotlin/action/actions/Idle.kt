package action.actions

import action.Action
import action.ActionParamList
import action.ActionPriority
import action.param

object Idle : Action(action = "idle"
    , actionPriority = ActionPriority.LowThird
    , description = fun () : String = IdleParamList().idleDescription()
    , executor = fun (idleParams : ActionParamList?) : String {
        return if (idleParams == null) IdleParamList().idleDescription()
        else IdleParamList(idleParams).idleDescription()
    }
) {
    class IdleParamList(val kInstanceName : String?, val moments : Int?) {

        constructor(actionParamList: ActionParamList) : this(
            kInstanceName = actionParamList.param<String>(0)
            , moments = actionParamList.param<Int>(1)
        )

        constructor(nullConstructor : Nothing? = null) : this(kInstanceName = null, moments = null)

        fun idleDescription() : String = "${Idle::class.simpleName} -> " +
                "IInstance named ${kInstanceNameOrT()} " +
                "putters around for ${momentsOrT()} moments"

        fun kInstanceNameOrT() = kInstanceName ?: String::class.simpleName

        fun momentsOrT() = moments ?: Int::class.simpleName

        fun actionParamList() : ActionParamList = listOf(kInstanceName, moments) as ActionParamList
    }
}