package actions.actionables

import actions.Action
import actions.ActionParamList
import actions.param
import templates.Register
import conditions.ISimpleConditionable.Companion.Always
import conditions.ISimpleConditionable.Companion.Never
import kotlinx.coroutines.ExperimentalCoroutinesApi
import templates.IInstance
import kotlin.time.ExperimentalTime

interface IInstantiator : IActionable {

    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override val actions : ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Instantiate to listOf(Never)
                , Destantiate to listOf(Always)
            )
        )

    companion object {
        @ExperimentalCoroutinesApi
        @ExperimentalUnsignedTypes
        @ExperimentalTime
        val Instantiate = Action(action = "instantiate"
            , description = null.instantiateDescription()
            , executor = fun (instantiateParamList : ActionParamList?) : String? {
                try {
                    instantiateParamList!!.instantiateParamRegister().addInstance(
                        kInstanceName = instantiateParamList.instantiateParamInstanceName(),
                        instanceTemplate = instantiateParamList.instantiateParamTemplate())
                    return instantiateParamList.instantiateDescription()
                } catch(e : Exception) { this.toString() + "exec(${instantiateParamList})" }
                    return null
            }
        )
        fun Action.instantiateParamList(template : IInstantiable, instanceName : String, register : Register) = listOf(template, instanceName, register)
        private fun ActionParamList.instantiateParamTemplate() = this.param<IInstantiable>(0)
        private fun ActionParamList.instantiateParamInstanceName() = this.param<String>(1)
        private fun ActionParamList.instantiateParamRegister() = this.param<Register>(2)
        private fun ActionParamList?.instantiateDescription() : String = "Action.Instantiate -> " +
            ("Instantiating " + (this?.instantiateParamTemplate()?.templateName ?: IInstantiable::class.simpleName) ) +
            (" as IInstance named " + (this?.instantiateParamInstanceName() ?: String::class.simpleName ) ) +
            (" in templates.Register " + (this?.instantiateParamRegister()?.kInstanceName ?: Register::class.simpleName ) )

        @ExperimentalUnsignedTypes
        val Destantiate = Action(action = "destantiate"
            , description = null.destantiateDescription()
            , executor = fun (destantiateParamList : ActionParamList?) : String? {
                try {
                        destantiateParamList!!.destantiateParamRegister().removeInstance(
                            kInstance = destantiateParamList.destantiateParamInstance()
                            , register = destantiateParamList.destantiateParamRegister()
                        )
                        return destantiateParamList.destantiateDescription()
                } catch(e : Exception) { this.toString() + "exec(${destantiateParamList})" }
                return null
            }
        )
        fun Action.destantiateParamList(kInstance : IInstance, register : Register) = listOf(kInstance, register)
        private fun ActionParamList.destantiateParamInstance() = this.param<IInstance>(0)
        private fun ActionParamList.destantiateParamRegister() = this.param<Register>(1)
        private fun ActionParamList?.destantiateDescription() : String = "Action.Destantiate -> " +
            ("Destantiating " + (this?.destantiateParamInstance()?.getInstanceName() ?: String::class.simpleName) ) +
            (" from templates.Register " + (this?.destantiateParamRegister()?.kInstanceName ?: Register::class.simpleName ) )
    }
}
