package time

import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import render.RenderActionPlex
import templates.Register
import kotlin.time.ExperimentalTime

object GlobalChannel {

    @ExperimentalCoroutinesApi
    const val mSecRenderDelay = 17L // 1/60th of sec
    const val mSecRenderStatusDelay = 63L // 1/16th of sec
    const val mSecPerceptionDelay = 1000L // 1 sec

    val logInfoChannel = Channel<String>()

    @ExperimentalCoroutinesApi
    suspend fun initLogInfoChannel() = coroutineScope {

        while (true) {
            while (!logInfoChannel.isEmpty) {
                println("logInfoChannel @ ${DateTime.now()} : ${logInfoChannel.receive()}")
            }
        }

    }

}
