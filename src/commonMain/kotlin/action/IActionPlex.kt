package action

import time.Moment

interface IActionPlex {

    @ExperimentalUnsignedTypes
    var actionPlex : ActionPlex

    fun getMoment() : Moment

    fun getMaxPlexSize() : Int
}

