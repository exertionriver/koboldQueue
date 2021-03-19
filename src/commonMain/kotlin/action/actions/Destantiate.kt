package action.actions

import ActionParamList
import action.Action
import param
import templates.IInstance
import templates.Register

@ExperimentalUnsignedTypes
object Destantiate : Action(action = "destantiate"
    , description = fun () : String = DestantiateParamList().destantiateDescription()
    , executor = fun (destantiateParamList : ActionParamList?) : String {
        if (destantiateParamList == null) return DestantiateParamList().destantiateDescription()

        DestantiateParamList(destantiateParamList).register!!.removeInstance(
            kInstance = DestantiateParamList(destantiateParamList).kInstance!!
            , register = DestantiateParamList(destantiateParamList).register!!
        )
        return DestantiateParamList(destantiateParamList).destantiateDescription()
    }
) {
    class DestantiateParamList(var kInstance : IInstance?, var register : Register?) {

        constructor(actionParamList: ActionParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
            , register = actionParamList.param<Register>(1)
        )

        constructor(nullConstructor : Nothing? = null) : this(kInstance = null, register = null)

        fun destantiateDescription() : String = "${Destantiate::class.simpleName} -> " +
                "Destantiating ${kInstanceNameOrT()} " +
                "from Register ${registerNameOrT()}"

        private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        private fun registerNameOrT() = register?.getInstanceName() ?: Register::class.simpleName

        fun actionParamList() = listOf(kInstance, register) as ActionParamList
    }

    @ExperimentalUnsignedTypes
    fun params(lambda: DestantiateParamList.() -> Unit) = DestantiateParamList().apply(lambda).actionParamList()
}