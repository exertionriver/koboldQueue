package action.actions

import action.Action
import action.ActionParamList
import action.ActionPriority
import action.param
import action.roles.IInstantiable
import action.roles.IObservable
import templates.IInstance
import templates.Register

object Look : Action(action = "look"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = LookParamList().lookDescription()
    , executor = fun (lookParams : ActionParamList?) : String {
        if (lookParams == null) return LookParamList().lookDescription()

        val lookObjects = LookParamList(lookParams).register!!.entries
            .filterValues { (it is IInstance) && (it is IObservable) && (it != LookParamList(lookParams).kInstance) }

        return if (!lookObjects.isNullOrEmpty() )
            LookParamList(lookParams).lookDescription().plus(": " +
                    lookObjects.map{ (it.value as IObservable).getDescription() }
                        .reduce{ lookResult : String, element -> lookResult.plus(" $element") })
        else
            LookParamList(lookParams).lookDescription()
        }
    ) {
        class LookParamList(val kInstance: IInstance?, val register: Register?) {

            constructor(actionParamList: ActionParamList) : this(
                kInstance = actionParamList.param<IInstance>(0)
                , register = actionParamList.param<Register>(1)
            )

            constructor(nullConstructor : Nothing? = null) : this(kInstance = null, register = null)

            fun lookDescription() : String = "${Look::class.simpleName} -> " +
                "IInstance named ${kInstanceNameOrT()} looks at IDescribable objects " +
                "in Register ${registerOrT()}"

            fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

            fun registerOrT() = register?.getInstanceName() ?: Register::class.simpleName

            fun actionParamList() = listOf(kInstance, register) as ActionParamList
        }

    }