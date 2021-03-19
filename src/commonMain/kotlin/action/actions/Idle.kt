package action.actions

import ActionParamList
import action.Action
import action.ActionPriority
import param
import templates.IInstance

object Idle : Action(action = "idle"
    , actionPriority = ActionPriority.LowThird
    , description = fun () : String = IdleParamList().idleDescription()
    , executor = fun (idleParams : ActionParamList?) : String {
        return if (idleParams == null) IdleParamList().idleDescription()
        else IdleParamList(idleParams).idleDescription()
    }
) {
    class IdleParamList(var kInstance : IInstance?, var moments : Int?) {

        constructor(actionParamList: ActionParamList?) : this(
            kInstance = actionParamList?.param<IInstance>(0)
            , moments = actionParamList?.param<Int>(1)
        )

        constructor(nullConstructor : Nothing? = null) : this(kInstance = null, moments = null)

        fun idleDescription() : String = "${Idle::class.simpleName} -> " +
                "IInstance named ${kInstanceNameOrT()} " +
                "putters around for ${momentsOrT()} moments"

        private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        private fun momentsOrT() = moments ?: Int::class.simpleName

        fun actionParamList() : ActionParamList = listOf(kInstance, moments) as ActionParamList
    }

    @ExperimentalUnsignedTypes
    fun params(lambda: IdleParamList.() -> Unit) = IdleParamList().apply(lambda).actionParamList()
}