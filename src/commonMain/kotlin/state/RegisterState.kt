package state

class RegisterState(override val state: String) : State(state) {

    companion object {
        val WatchState = RegisterState("watch")
    }
}
