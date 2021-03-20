package action.actions

import ParamList
import action.Action
import action.ActionPriority
import param
import templates.IInstance

object Reflect : Action(action = "reflect"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = ReflectParamList().reflectDescription()
    , executor = fun (reflectParams : ParamList?) : String {
        return if (reflectParams == null) ReflectParamList().reflectDescription()
        else ReflectParamList(reflectParams).reflectDescription()
    }
) {
    class ReflectParamList(var kInstance: IInstance?) {

        constructor(actionParamList: ParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
        )

        constructor(nullConstructor: Nothing? = null) : this(kInstance = null)

        fun reflectDescription() : String = "${Reflect::class.simpleName} -> " +
            "IInstance named ${kInstanceNameOrT()} reflects upon the situation"

        private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        fun actionParamList() = listOf(kInstance) as ParamList
    }

    @ExperimentalUnsignedTypes
    fun params(lambda: ReflectParamList.() -> Unit) = ReflectParamList().apply(lambda).actionParamList()
}