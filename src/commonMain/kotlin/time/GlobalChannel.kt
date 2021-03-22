package time

import com.soywiz.klock.DateTime
import com.soywiz.korge.view.View
import com.soywiz.korio.async.launch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel


//TODO: route debug info to logInfoChannel

object GlobalChannel {

    @ExperimentalCoroutinesApi
    const val mSecRenderDelay = 17L // 1/60th of sec
    const val mSecRenderStatusDelay = 63L // 1/16th of sec
    const val mSecPerceptionDelay = 1000L // 1 sec

    val logInfoChannel = Channel<String>()
    val viewRemoveChannel = Channel<View>()

    @ExperimentalCoroutinesApi
    suspend fun initLogInfoChannel() = coroutineScope {

        while (true) {
            if (!logInfoChannel.isEmpty) delay(mSecRenderDelay * 3)
            while (!logInfoChannel.isEmpty) {
                println("logInfoChannel @ ${DateTime.now()} : ${logInfoChannel.receive()}")
            }
        }

    }

    @ExperimentalCoroutinesApi
    suspend fun initViewRemoveChannel() {

        launch(CoroutineScope(Dispatchers.Default).coroutineContext) {
            while (true) {
                while (!viewRemoveChannel.isEmpty) {
                    val viewToRemove = viewRemoveChannel.receive()
                    println("viewRemoveChannel @ ${DateTime.now()} : $viewToRemove")
                 //   viewToRemove.removeFromParent()
                }

                //   println("viewRemoveChannel @ ${DateTime.now()}")
            }
        }

    }

}
