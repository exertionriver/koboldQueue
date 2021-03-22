package action

import kotlinx.coroutines.ExperimentalCoroutinesApi
import time.Moment
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
interface IActionPlex {

    var actionPlex : ActionPlex

    fun getMoment() : Moment

    fun getMaxPlexSize() : Int
}

