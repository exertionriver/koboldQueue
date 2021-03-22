package action.actions

import ParamList
import action.Action
import action.ActionPriority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import param
import templates.IInstance
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
object Idle : Action(actionLabel = "idle"
    , actionPriority = ActionPriority.LowThird
    , description = fun () : String = IdleParamList().idleDescription()
    , executor = fun (idleParams : ParamList?) : String {
        return if (idleParams == null) IdleParamList().idleDescription()
        else IdleParamList(idleParams).idleDescription()
    }
) {
    class IdleParamList(var kInstance : IInstance?, var moments : Int?) {

        constructor(actionParamList: ParamList?) : this(
            kInstance = actionParamList?.param<IInstance>(0)
            , moments = actionParamList?.param<Int>(1)
        )

        constructor() : this(kInstance = null, moments = null)

        fun idleDescription() : String = "${Idle::class.simpleName} -> " +
                "IInstance named ${kInstanceNameOrT()} " +
                "putters around for ${momentsOrT()} moments"

        private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        private fun momentsOrT() = moments ?: Int::class.simpleName

        @Suppress("UNCHECKED_CAST")
        fun actionParamList() : ParamList = listOf(kInstance, moments) as ParamList
    }

    fun params(lambda: IdleParamList.() -> Unit) = IdleParamList().apply(lambda).actionParamList()
}