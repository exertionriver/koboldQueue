package action.roles

import kotlinx.coroutines.ExperimentalCoroutinesApi
import templates.IInstance
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
interface IInstantiable {

    fun getTemplateName() : String

    fun getInstance(kInstanceName : String) : IInstance

}
