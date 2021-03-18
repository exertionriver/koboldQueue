package action.roles

import action.IAction

interface IObservable : IAction {

    fun getDescription() : String
}
