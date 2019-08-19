package maderski.bluetoothautoplaymusic.controls.wakelockcontrol

import android.content.Context
import android.os.PowerManager
import android.util.Log

import java.util.Calendar

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

/**
 * Created by Jason on 1/5/16.
 */

//Uses the singleton pattern, only want one instance of ScreenONLock
class ScreenONLock(private val preferences: BAPMPreferences) {

    private var mWakeLock: PowerManager.WakeLock? = null

    //Enable WakeLock
    fun enableWakeLock(context: Context) {
        //Set Screen Brightness(Dim: 6 / Bright: 10)
        val screenBrightness: Int = if (preferences.getAutoBrightness()) {
            isDark(context)
        } else {
            getManualScreenBrightness(context)
        }

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP or screenBrightness, WAKELOCK_TAG)

        try {
            mWakeLock?.acquire()
            Log.d(TAG, "Wakelock enabled")
        } catch (e: Exception) {
            Log.e(TAG, "WakeLock Error: " + e.message)
        }

    }

    private fun getManualScreenBrightness(context: Context): Int {
        val screenBrightness: Int

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val currentTime = hour * 100 + minute
        val brightSetTime = preferences.getBrightTime()
        val dimSetTime = preferences.getDimTime()

        if (currentTime >= dimSetTime || currentTime <= brightSetTime) {
            screenBrightness = 6
        } else
            screenBrightness = 10

        return screenBrightness
    }

    //Disable and release WakeLock
    fun releaseWakeLock() {
        if (mWakeLock != null && mWakeLock!!.isHeld) {
            try {
                mWakeLock!!.release()
                mWakeLock = null
                Log.d(TAG, "Wakelock: " + "disabled")
            } catch (e: Exception) {
                Log.e(TAG, "WakeLock: " + "error " + e.message)
            }

        } else {
            Log.i(TAG, "Wakelock: " + "Not Held")
        }
    }

    //Return dimmer screen brightness if Dark
    private fun isDark(context: Context): Int {
        val dark: Int
        val c = Calendar.getInstance()
        val ss = SunriseSunset(context)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val currentTime = hour * 100 + minute
        val sunriseTime = ss.sunrise
        val sunsetTime = ss.sunset

        Log.d(TAG, "Current: " + Integer.toString(currentTime) +
                " SR: " + Integer.toString(sunriseTime) +
                " SS: " + Integer.toString(sunsetTime))

        if (currentTime >= 1200) {
            dark = if (currentTime >= sunsetTime) 6 else 10
        } else {
            dark = if (currentTime <= sunriseTime) 6 else 10
        }

        Log.d(TAG, "dark: " + Integer.toString(dark))
        return dark
    }

    fun wakeLockHeld(): Boolean {
        return mWakeLock != null && mWakeLock!!.isHeld
    }

    companion object {
        private const val TAG = "ScreenONLock"
        private const val WAKELOCK_TAG = "maderski.bluetoothautoplaymusic.controls.wakelockcontrol:StayON"
    }

}
