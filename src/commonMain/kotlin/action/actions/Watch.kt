package action.actions

import action.*
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
    class WatchParamList(val kInstanceName: String?, val register: Register?) {

        constructor(actionParamList: ActionParamList) : this(
            kInstanceName = actionParamList.param<String>(0)
            , register = actionParamList.param<Register>(1)
        )

        constructor(nullConstructor : Nothing? = null) : this(kInstanceName = null, register = null)

        fun watchDescription() : String = "${Watch::class.simpleName} -> " +
            "IInstance named ${kInstanceName ?: String::class.simpleName} watches IDescribable objects " +
            "in Register ${register?.getInstanceName() ?: Register::class.simpleName}"


        fun actionParamList() = listOf(kInstanceName, register) as ActionParamList
    }
}