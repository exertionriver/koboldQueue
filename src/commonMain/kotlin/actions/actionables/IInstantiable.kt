package actions.actionables

import templates.IInstance

interface IInstantiable {

    val templateName : String

    fun getInstance(kInstanceName : String) : IInstance

}
