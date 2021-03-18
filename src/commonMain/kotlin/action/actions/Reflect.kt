package action.actions

import action.Action
import action.ActionParamList
import action.ActionPriority
import action.param

object Reflect : Action(action = "reflect"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = ReflectParamList().reflectDescription()
    , executor = fun (reflectParams : ActionParamList?) : String {
        return if (reflectParams == null) ReflectParamList().reflectDescription()
        else ReflectParamList(reflectParams).reflectDescription()
    }
) {
    class ReflectParamList(val kInstanceName: String?) {

        constructor(actionParamList: ActionParamList) : this(
            kInstanceName = actionParamList.param<String>(0)
        )

        constructor(nullConstructor: Nothing? = null) : this(kInstanceName = null)

        fun reflectDescription() : String = "${Reflect::class.simpleName} -> " +
            "IInstance named ${kInstanceName ?: String::class.simpleName} reflects upon the situation"

        fun actionParamList() = listOf(kInstanceName) as ActionParamList
    }
}