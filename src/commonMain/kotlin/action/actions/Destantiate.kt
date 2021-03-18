package action.actions

import action.Action
import action.ActionParamList
import action.param
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
    class DestantiateParamList(val kInstance : IInstance?, val register : Register?) {

        constructor(actionParamList: ActionParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
            , register = actionParamList.param<Register>(1)
        )

        constructor(nullConstructor : Nothing? = null) : this(kInstance = null, register = null)

        fun destantiateDescription() : String = "${Destantiate::class.simpleName} -> " +
                "Destantiating ${kInstance?.getInstanceName() ?: String::class.simpleName} " +
                "from Register ${register?.getInstanceName() ?: Register::class.simpleName}"

        fun actionParamList() = listOf(kInstance, register) as ActionParamList

    }
}