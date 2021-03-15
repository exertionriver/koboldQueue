package time

import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import templates.Register
import kotlin.time.ExperimentalTime

object GlobalTimer {

    @ExperimentalCoroutinesApi
    private const val mSecGlobalDelay = 1L // 1/1000th of a sec
    private const val mSecRenderDelay = 17L // 1/60th of sec
    private const val mSecInputDelay = 62L // 1/16th of sec
    private const val mSecTextRenderDelay = 62L // 1/16th of sec
    private const val mSecPerceptionDelay = 1000L // 1 sec

    val globalChannel = Channel<String>(capacity = 100)
    val renderChannel = Channel<String>(capacity = 100)
    val inputChannel = Channel<String>(capacity = 100)
    val textRenderChannel = Channel<String>(capacity = 100)
    val perceptionChannel = Channel<String>(capacity = 100)

    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    private suspend fun doGlobalQueue() : Timer = coroutineScope {
        val newTimer = Timer()
        while (!globalChannel.isEmpty) {
            println("global @ ${ DateTime.now() } ${globalChannel.receive()}")
        }
        delay(mSecGlobalDelay)
        return@coroutineScope newTimer
    }

    @ExperimentalCoroutinesApi
    private suspend fun doRenderQueue() : Timer = coroutineScope {
        val newTimer = Timer()
        while (!renderChannel.isEmpty) {
            println("render @ ${ DateTime.now() } ${renderChannel.receive()}")
        }
        delay(mSecRenderDelay)
        return@coroutineScope newTimer
    }

    @ExperimentalCoroutinesApi
    @ExperimentalUnsignedTypes
    @ExperimentalTime
    private suspend fun doInputQueue(globalRegister: Register) : Timer = coroutineScope {
        val newTimer = Timer()

        while (!inputChannel.isEmpty) {
            println("input @ ${ DateTime.now() } ${inputChannel.receive()}")
        }
        delay(mSecInputDelay)
        return@coroutineScope newTimer
    }

    @ExperimentalCoroutinesApi
    private suspend fun doTextRenderQueue() : Timer = coroutineScope {
        val newTimer = Timer()
        while (!textRenderChannel.isEmpty) {
            println("textRender @ ${ DateTime.now() } ${textRenderChannel.receive()}")
        }
        delay(mSecTextRenderDelay)
        return@coroutineScope newTimer
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    private suspend fun doPerceptionQueue(timer : Timer) : Timer = coroutineScope {
        val newTimer = Timer()
        println("perception @ ${ DateTime.now() } PT:${timer.getMillisecondsElapsed()}")
        while (!perceptionChannel.isEmpty) {
            println("perception @ ${ DateTime.now() } ${perceptionChannel.receive()}")
        }
        delay(mSecPerceptionDelay)
        return@coroutineScope newTimer
    }

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    suspend fun perform(globalRegister : Register) = coroutineScope {

        println("starting global timer @ ${DateTime.now()}")

        var perceptionTimer = Timer()
        var globalRegTimer = Timer()

        coroutineScope {
            launch {
                while (true) {
                    doGlobalQueue()
                }
            }
            launch {
                while (true) {
                    doRenderQueue()
                }
            }
            launch {
                while (true) {
                    doInputQueue(globalRegister)
                }
            }
            launch {
                while (true) {
                    doTextRenderQueue()
                }
            }
            launch {
                while (true) {
                    perceptionTimer = doPerceptionQueue(perceptionTimer)
                }
            }
            launch {
                while (true) {
                    globalRegTimer = globalRegister.perform(globalRegTimer, globalRegister)
                }
            }
        }

    return@coroutineScope
    }
}
