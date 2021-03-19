package action.actions

import action.*
import templates.IInstance
import templates.Register

@ExperimentalUnsignedTypes
object Watch : Action(action = "watch"
    , momentsToPrepare = 1, momentsToExecute = 3
    , actionType = ActionType.OneTimeExec
    , plexSlotsRequired = 2
    , description = fun () : String = WatchParamList().watchDescription()
    , executor = fun (watchParams : ActionParamList?) : String {
        return if (watchParams == null) WatchParamList().watchDescription()
        else WatchParamList(watchParams).watchDescription()
    }
) {
    class WatchParamList(val kInstance: IInstance?, val register: Register?) {

        constructor(actionParamList: ActionParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
            , register = actionParamList.param<Register>(1)
        )

        constructor(nullConstructor : Nothing? = null) : this(kInstance = null, register = null)

        fun watchDescription() : String = "${Watch::class.simpleName} -> " +
            "IInstance named ${kInstanceNameOrT()} watches IDescribable objects " +
            "in Register ${registerNameOrT()}"

        fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        fun registerNameOrT() = register?.getInstanceName() ?: Register::class.simpleName

        fun actionParamList() = listOf(kInstance, register) as ActionParamList
    }
}