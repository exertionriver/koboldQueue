package action.roles

import action.IAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
interface IObservable : IAction {

    fun getDescription() : String
}
