package state

class ActionState(override val state: String) : State(state) {

    companion object {
        val ActionQueue = ActionState("actionQueue")
        val ActionPrepare = ActionState("actionPrepare")
        val ActionExecute = ActionState("actionExecute")
        val ActionRecover = ActionState("actionRecover")
        val ActionNotFound = ActionState("actionNotFound")

        val InProcess = listOf(ActionPrepare, ActionExecute, ActionRecover)
        val Interruptable = listOf(ActionPrepare, ActionExecute, ActionRecover)
        val Preemptable = listOf(ActionPrepare)
    }

    override fun toString() = "${ActionState::class.simpleName}($state)"
}
