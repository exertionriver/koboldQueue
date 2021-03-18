package action.actions

import action.Action
import action.ActionParamList
import action.param
import action.roles.IInstantiable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import templates.Register
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
object Instantiate : Action(action = "instantiate"
    , description = fun () : String = InstantiateParamList().instantiateDescription()
    , executor = fun (instantiateParamList : ActionParamList?) : String {
        if (instantiateParamList == null) return InstantiateParamList().instantiateDescription()

        InstantiateParamList(instantiateParamList).register!!.addInstance(
            kInstanceName = InstantiateParamList(instantiateParamList).kInstanceName!!
            , instanceTemplate = InstantiateParamList(instantiateParamList).template!!)
        return InstantiateParamList(instantiateParamList).instantiateDescription()
    }
) {
    class InstantiateParamList(val template : IInstantiable?, val kInstanceName : String?, val register : Register?) {

        constructor(actionParamList: ActionParamList) : this(
            template = actionParamList.param<IInstantiable>(0)
            , kInstanceName = actionParamList.param<String>(1)
            , register = actionParamList.param<Register>(2)
        )

        constructor(nullConstructor : Nothing? = null) : this(template = null, kInstanceName = null, register = null)

        fun instantiateDescription() : String = "${Instantiate::class.simpleName} -> " +
                "Instantiating ${template?.templateName ?: IInstantiable::class.simpleName} " +
                "as IInstance named ${kInstanceName ?: String::class.simpleName} " +
                "in Register ${register?.getInstanceName() ?: Register::class.simpleName}"

        fun actionParamList() = listOf(template, kInstanceName, register) as ActionParamList
    }
}
