package action

import templates.Moment

interface IActionPlex {

    @ExperimentalUnsignedTypes
    var actionPlex : ActionPlex

    fun getMoment() : Moment

    fun getMaxPlexSize() : Int
}

