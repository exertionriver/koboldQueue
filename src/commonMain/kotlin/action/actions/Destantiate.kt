package action.actions

import ParamList
import action.Action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import param
import templates.IInstance
import templates.Register
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
object Destantiate : Action(actionLabel = "destantiate"
    , description = fun () : String = DestantiateParamList().destantiateDescription()
    , executor = fun (destantiateParamList : ParamList?) : String {
        if (destantiateParamList == null) return DestantiateParamList().destantiateDescription()

        DestantiateParamList(destantiateParamList).register!!.removeInstance(
            kInstance = DestantiateParamList(destantiateParamList).kInstance!!
            , register = DestantiateParamList(destantiateParamList).register!!
        )
        return DestantiateParamList(destantiateParamList).destantiateDescription()
    }
) {
    class DestantiateParamList(var kInstance : IInstance?, var register : Register?) {

        constructor(actionParamList: ParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
            , register = actionParamList.param<Register>(1)
        )

        constructor(nullConstructor : Nothing? = null) : this(kInstance = null, register = null)

        fun destantiateDescription() : String = "${Destantiate::class.simpleName} -> " +
                "Destantiating ${kInstanceNameOrT()} " +
                "from Register ${registerNameOrT()}"

        private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        private fun registerNameOrT() = register?.getInstanceName() ?: Register::class.simpleName

        fun actionParamList() = listOf(kInstance, register) as ParamList
    }

    @ExperimentalUnsignedTypes
    fun params(lambda: DestantiateParamList.() -> Unit) = DestantiateParamList().apply(lambda).actionParamList()
}