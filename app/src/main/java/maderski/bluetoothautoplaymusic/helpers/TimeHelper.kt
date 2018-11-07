package maderski.bluetoothautoplaymusic.helpers

import java.util.Calendar

/**
 * Created by Jason on 3/14/17.
 */

class TimeHelper(private var startTime: Int, private var endTime: Int, private val mCurrentTime: Int) {

    // Return result on whether Maps/Waze can launch or not
    val isWithinTimeSpan: Boolean
        get() = mCurrentTime in startTime..endTime

    init {
        if (endTime < startTime) {
            if (mCurrentTime >= 1200) {
                endTime += 2400
            } else {
                startTime = 0
            }
        }
    }

    companion object {
        fun get12hrTime(time: Int): String {
            // Get minutes
            var tempToGetMinutes = time
            while (tempToGetMinutes > 60) {
                tempToGetMinutes -= 100
            }
            val minutes = if (tempToGetMinutes > 9) Integer.toString(tempToGetMinutes) else "0" + Integer.toString(tempToGetMinutes)

            // Get hour
            var tempToGetHour = (time - tempToGetMinutes) / 100
            if (tempToGetHour == 0) {
                tempToGetHour = 24
            }
            val hour = if (tempToGetHour > 12) Integer.toString(tempToGetHour - 12) else Integer.toString(tempToGetHour)
            val AMPM = if (tempToGetHour > 11 && tempToGetHour < 24) "PM" else "AM"

            return "$hour:$minutes $AMPM"
        }

        val current24hrTime: Int
            get() {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                return hour * 100 + minute
            }
    }
}
