package maderski.bluetoothautoplaymusic.controls.wakelockcontrol

import android.content.Context
import android.util.Log

import java.util.Calendar

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location

/**
 * Created by Jason on 2/7/16.
 *
 * Get Sunrise and Sunset Times as Strings
 */

class SunriseSunset(context: Context) {

    private val sunriseTime: String
    private val sunsetTime: String

    //Return Sunrise time as a single integer number
    val sunrise: Int
        get() = getTime(sunriseTime)

    //Return Sunrise time as a single integer number
    val sunset: Int
        get() = getTime(sunsetTime)

    init {
        val today = Calendar.getInstance()
        val currentLocation = CurrentLocation(context)
        val location = Location(currentLocation.latitude, currentLocation.longitude)
        val calculator = SunriseSunsetCalculator(location, Calendar.getInstance().timeZone)
        Log.d(TAG, "Lat " + currentLocation.latitude + " Long " + currentLocation.longitude)
        sunriseTime = calculator.getCivilSunriseForDate(today)
        sunsetTime = calculator.getCivilSunsetForDate(today)
        Log.d(TAG, "sunrise: $sunriseTime sunset: $sunsetTime")
    }

    //Convert Time String into a single integer number
    private fun getTime(inputTime: String): Int {
        val split = inputTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val outputTime = Integer.parseInt(split[0]) * 100 + Integer.parseInt(split[1])
        Log.d(TAG, Integer.toString(outputTime))
        return outputTime
    }

    companion object {
        private val TAG = "SunriseSunset"
    }
}
