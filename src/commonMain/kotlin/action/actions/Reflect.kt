package action.actions

import action.Action
import action.ActionParamList
import action.ActionPriority
import action.param
import templates.IInstance
import templates.Register

object Reflect : Action(action = "reflect"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = ReflectParamList().reflectDescription()
    , executor = fun (reflectParams : ActionParamList?) : String {
        return if (reflectParams == null) ReflectParamList().reflectDescription()
        else ReflectParamList(reflectParams).reflectDescription()
    }
) {
    class ReflectParamList(val kInstance: IInstance?) {

        constructor(actionParamList: ActionParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
        )

        constructor(nullConstructor: Nothing? = null) : this(kInstance = null)

        fun reflectDescription() : String = "${Reflect::class.simpleName} -> " +
            "IInstance named ${kInstanceNameOrT()} reflects upon the situation"

        fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        fun actionParamList() = listOf(kInstance) as ActionParamList
    }
}