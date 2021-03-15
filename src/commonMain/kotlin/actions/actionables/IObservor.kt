package actions.actionables

import actions.Action
import actions.ActionParamList
import actions.ActionType.Companion.OneTimeExec
import actions.param
import templates.Register
import kotlin.time.ExperimentalTime

interface IObservor : IActionable {

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    override val actions : ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Look to null
                , Watch to null
                , Reflect to null
            )
        )

    companion object {
        val Look = Action(action = "look"
            , momentsToPrepare = 1, momentsToExecute = 1
            , description = null.lookDescription()
            , executor = fun (lookParams : ActionParamList?) : String? {
                try {
                    val lookObjects = lookParams!!.lookParamRegister().entries
                        .filterValues { (it is IObservable) && (it.getInstanceName() != lookParams.lookParamInstanceName()) }

                    return if (!lookObjects.isNullOrEmpty() )
                        lookParams.lookDescription().plus(": " +
                        lookObjects.map{ (it.value as IObservable).getDescription() }
                            .reduce{ lookResult : String, element -> lookResult.plus(" $element") })
                    else
                        lookParams.lookDescription()

                } catch(e : Exception) { this.toString() + "exec(${lookParams})" }
                return null
            }
        )
        fun Action.lookParamList(kInstanceName : String, register : Register) = listOf(kInstanceName, register)
        private fun ActionParamList.lookParamInstanceName() = this.param<String>(0)
        private fun ActionParamList.lookParamRegister() = this.param<Register>(1)
        private fun ActionParamList?.lookDescription() : String = "Action.Look -> " +
                ("IInstance named " + (this?.lookParamInstanceName() ?: String::class.simpleName ) + " looks at IDescribable objects" ) +
                (" in Register " + (this?.lookParamRegister()?.kInstanceName ?: Register::class.simpleName ) )

        @ExperimentalUnsignedTypes
        val Watch = Action(action = "watch"
            , momentsToPrepare = 1, momentsToExecute = 3
            , actionType = OneTimeExec
            , plexSlotsRequired = 2
            , description = null.watchDescription()
            , executor = fun (watchParams : ActionParamList?) : String? {
                try {
                    return watchParams.watchDescription()
                } catch(e : Exception) { this.toString() + "exec(${watchParams})" }
                return null
            }
        )
        fun Action.watchParamList(kInstanceName : String, register : Register) = listOf(kInstanceName, register)
        private fun ActionParamList.watchParamInstanceName() = this.param<String>(0)
        private fun ActionParamList.watchParamRegister() = this.param<Register>(1)
        private fun ActionParamList?.watchDescription() : String = "Action.Watch -> " +
                ("IInstance named " + (this?.watchParamInstanceName() ?: String::class.simpleName ) + " watches IDescribable objects" ) +
                (" in Register " + (this?.watchParamRegister()?.kInstanceName ?: Register::class.simpleName ) )

        val Reflect = Action(action = "reflect"
            , description = null.reflectDescription()
            , executor = fun (reflectParams : ActionParamList?) : String? {
                try {
                    return reflectParams.reflectDescription()
                } catch(e : Exception) { this.toString() + "exec(${reflectParams})" }
                return null
            }
        )
        fun Action.reflectParamList(kInstanceName : String) = listOf(kInstanceName)
        private fun ActionParamList.reflectParamInstanceName() = this.param<String>(0)
        private fun ActionParamList?.reflectDescription() : String = "Action.Reflect -> " +
                ("IInstance named " + (this?.reflectParamInstanceName() ?: String::class.simpleName ) + " reflects upon the situation")

    }
}