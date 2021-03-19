package action.actions

import ActionParamList
import action.Action
import action.roles.IInstantiable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import param
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
    class InstantiateParamList(var template : IInstantiable?, var kInstanceName : String?, var register : Register?) {

        constructor(actionParamList: ActionParamList) : this(
            template = actionParamList.param<IInstantiable>(0)
            , kInstanceName = actionParamList.param<String>(1)
            , register = actionParamList.param<Register>(2)
        )

        constructor(nullConstructor : Nothing? = null) : this(template = null, kInstanceName = null, register = null)

        fun instantiateDescription() : String = "${Instantiate::class.simpleName} -> " +
                "Instantiating ${templateNameOrT()} " +
                "as IInstance named ${kInstanceNameOrT()} " +
                "in Register ${registerNameOrT()}"

        private fun templateNameOrT() = template?.getTemplateName() ?: IInstantiable::class.simpleName

        private fun kInstanceNameOrT() = kInstanceName ?: String::class.simpleName

        private fun registerNameOrT() = register?.getInstanceName() ?: Register::class.simpleName

        fun actionParamList() = listOf(template, kInstanceName, register) as ActionParamList
    }

    @ExperimentalUnsignedTypes
    fun params(lambda: InstantiateParamList.() -> Unit) = InstantiateParamList().apply(lambda).actionParamList()

}