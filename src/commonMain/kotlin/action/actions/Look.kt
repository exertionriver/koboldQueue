package action.actions

import ParamList
import action.Action
import action.ActionPriority
import action.roles.IObservable
import param
import templates.IInstance
import templates.Register

object Look : Action(actionLabel = "look"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = LookParamList().lookDescription()
    , executor = fun (lookParams : ParamList?) : String {
        if (lookParams == null) return LookParamList().lookDescription()

        val lookObjects = LookParamList(lookParams).register!!.entries
            .filterKeys { (it is IObservable) && (it != LookParamList(lookParams).kInstance) }

        return if (!lookObjects.isNullOrEmpty() )
            LookParamList(lookParams).lookDescription().plus(": " +
                    lookObjects.map{ (it.key as IObservable).getDescription() }
                        .reduce{ lookResult : String, element -> lookResult.plus(" $element") })
        else
            LookParamList(lookParams).lookDescription()
        }
    ) {
        class LookParamList(var kInstance: IInstance?, var register: Register?) {

            constructor(actionParamList: ParamList) : this(
                kInstance = actionParamList.param<IInstance>(0)
                , register = actionParamList.param<Register>(1)
            )

            constructor(nullConstructor : Nothing? = null) : this(kInstance = null, register = null)

            fun lookDescription() : String = "${Look::class.simpleName} -> " +
                "IInstance named ${kInstanceNameOrT()} looks at IDescribable objects " +
                "in Register ${registerOrT()}"

            private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

            private fun registerOrT() = register?.getInstanceName() ?: Register::class.simpleName

            fun actionParamList() = listOf(kInstance, register) as ParamList
        }

    @ExperimentalUnsignedTypes
    fun params(lambda: LookParamList.() -> Unit) = LookParamList().apply(lambda).actionParamList()
}