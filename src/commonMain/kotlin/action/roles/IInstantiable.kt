package action.roles

import templates.IInstance

interface IInstantiable {

    fun getTemplateName() : String

    fun getInstance(kInstanceName : String) : IInstance

}
