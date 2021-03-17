package time

import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import render.RenderActionPlex
import templates.Register
import kotlin.time.ExperimentalTime

object GlobalTimer {

    @ExperimentalCoroutinesApi
    const val mSecRenderDelay = 17L // 1/60th of sec
    const val mSecRenderStatusDelay = 63L // 1/16th of sec
    const val mSecPerceptionDelay = 1000L // 1 sec

 //   @ExperimentalTime
 //   @ExperimentalUnsignedTypes
//    @ExperimentalCoroutinesApi
 // //  suspend fun doRenderQueue(timer : Timer) : Timer = coroutineScope {

   //     val checkTimer = Timer()

 //       if (timer.getMillisecondsElapsed() >= mSecRenderStatusDelay) {
        //    println("render @ ${DateTime.now()} RT:${timer.getMillisecondsElapsed()}")

     //       return@coroutineScope withContext(Dispatchers.Unconfined) {
       //         RenderActionPlex.perform(timer)
         //   }

     //       println("Render -- checktimer: ${checkTimer.getMillisecondsElapsed()} timer: ${timer.getMillisecondsElapsed()}")

         //   return@coroutineScope render

       // } //else delay(mSecRenderStatusDelay)

       // return@coroutineScope timer
 //   }

    @ExperimentalUnsignedTypes
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    suspend fun perform(globalRegister : Register) {

        println("starting global timer @ ${DateTime.now()}")

        var renderTimer = Timer()
        var globalRegTimer = Timer()

        val coroutineScopeU = CoroutineScope(Dispatchers.Unconfined)
        val coroutineScopeD = CoroutineScope(Dispatchers.Default)

        var regMomentCounter = 0
        var renMomentCounter = 0

        var regmoment = globalRegister.moment.milliseconds * 2

//        while (true) {

  //          val renCheckTimer = Timer()

     //       if (renderTimer.getMillisecondsElapsed() / mSecRenderDelay > renMomentCounter) {

         //       renMomentCounter = (renderTimer.getMillisecondsElapsed() / mSecRenderDelay).toInt()

         //       renderTimer = withContext(coroutineScopeD.coroutineContext) { RenderActionPlex.perform(renderTimer) }
                    //withContext(coroutineScope.coroutineContext) { doRenderQueue(renderTimer) }

           //     println("Render @ ${DateTime.now()} CT:${renCheckTimer.getMillisecondsElapsed()}, RT:${renderTimer.getMillisecondsElapsed()}, $renMomentCounter")

       //     }

     //       val regCheckTimer = Timer()

        //    if (globalRegTimer.getMillisecondsElapsed() / globalRegister.moment.milliseconds > regMomentCounter) {

       //         regMomentCounter = (globalRegTimer.getMillisecondsElapsed() / globalRegister.moment.milliseconds).toInt()

       //     coroutineScope {
       //         for (entry in globalRegister.entries.toMap()) {
                    // println("GlobalTimer @ ${DateTime.now()} RT:${globalRegTimer.getMillisecondsElapsed()}")
       //             val innerCheckTimer = Timer()
                        /*
                    var instanceTimer = globalRegTimer
                    instanceTimer =
                        withContext(coroutineScopeU.coroutineContext) {
                            entry.value.perform(globalRegTimer, globalRegister)
                        }
*/
  //                  println("Register inner ${globalRegister.kInstanceName} for entry: ${entry.value.getInstanceName()} -- innerChecktimer: ${innerCheckTimer.getMillisecondsElapsed()} $regMomentCounter")

     //           }
     //       }
        //        println("Register ${globalRegister.kInstanceName} checktimer: ${regCheckTimer.getMillisecondsElapsed()} $regMomentCounter")
                //delay(globalRegister.moment.milliseconds)
         //   }//else delay(globalRegister.moment.milliseconds)

    //    }
    }
}
