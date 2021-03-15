package time

import com.soywiz.klock.DateTime

class Timer(val initTimer : DateTime = DateTime.now() ) {

    fun getMillisecondsElapsed() : Int = (DateTime.now().unixMillis - initTimer.unixMillis).toInt()

    override fun toString() = "time.Timer(${initTimer}) : ${getMillisecondsElapsed()}"
}
