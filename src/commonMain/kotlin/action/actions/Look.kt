package action.actions

import action.Action
import action.ActionParamList
import action.ActionPriority
import action.param
import action.roles.IObservable
import templates.IInstance
import templates.Register

object Look : Action(action = "look"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = LookParamList().lookDescription()
    , executor = fun (lookParams : ActionParamList?) : String {
        if (lookParams == null) return LookParamList().lookDescription()

        val lookObjects = LookParamList(lookParams).register!!.entries
            .filterValues { (it is IInstance) && (it is IObservable) && (it.getInstanceName() != LookParamList(lookParams).kInstanceName) }

        return if (!lookObjects.isNullOrEmpty() )
            LookParamList(lookParams).lookDescription().plus(": " +
                    lookObjects.map{ (it.value as IObservable).getDescription() }
                        .reduce{ lookResult : String, element -> lookResult.plus(" $element") })
        else
            LookParamList(lookParams).lookDescription()
        }
    ) {
        class LookParamList(val kInstanceName: String?, val register: Register?) {

            constructor(actionParamList: ActionParamList) : this(
                kInstanceName = actionParamList.param<String>(0)
                , register = actionParamList.param<Register>(1)
            )

            constructor(nullConstructor : Nothing? = null) : this(kInstanceName = null, register = null)

            fun lookDescription() : String = "${Look::class.simpleName} -> " +
                "IInstance named ${kInstanceName ?: String::class.simpleName} looks at IDescribable objects " +
                "in Register ${register?.getInstanceName() ?: Register::class.simpleName}"

            fun actionParamList() = listOf(kInstanceName, register) as ActionParamList
        }

    }