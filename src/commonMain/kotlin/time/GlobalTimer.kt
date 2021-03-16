package time

import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import render.RenderActionPlex
import templates.Register
import kotlin.time.ExperimentalTime

object GlobalTimer {

    @ExperimentalCoroutinesApi
    const val mSecRenderDelay = 17L // 1/60th of sec
    const val mSecPerceptionDelay = 1000L // 1 sec

    @ExperimentalTime
    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    suspend fun doRenderQueue(timer : Timer) : Timer = coroutineScope {

        if (timer.getMillisecondsElapsed() >= mSecRenderDelay) {
        //    println("render @ ${DateTime.now()} RT:${timer.getMillisecondsElapsed()}")

            return@coroutineScope withContext(Dispatchers.Unconfined) {
                RenderActionPlex.perform(timer)
            }
        } else delay(mSecRenderDelay)

        return@coroutineScope timer
    }

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    suspend fun perform(globalRegister : Register) {

        println("starting global timer @ ${DateTime.now()}")

        var renderTimer = Timer()
        var globalRegTimer = Timer()

        val coroutineScope = CoroutineScope(Dispatchers.Unconfined)

        while (true) {
            renderTimer = withContext(coroutineScope.coroutineContext) { doRenderQueue(renderTimer) }

            for (entry in globalRegister.entries.toMap()) {
               // println("GlobalTimer @ ${DateTime.now()} RT:${globalRegTimer.getMillisecondsElapsed()}")

                var instanceTimer = globalRegTimer
                instanceTimer =
                    withContext(coroutineScope.coroutineContext) { entry.value.perform(globalRegTimer, globalRegister) }
            }
        }

    }
}
