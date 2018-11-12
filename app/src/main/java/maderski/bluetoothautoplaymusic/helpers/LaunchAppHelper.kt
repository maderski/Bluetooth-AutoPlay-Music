package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringDef
import android.util.Log

import java.util.ArrayList
import java.util.Calendar

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.ui.activities.LaunchBAPMActivity
import maderski.bluetoothautoplaymusic.ui.activities.MainActivity

/**
 * Created by Jason on 12/8/15.
 */
class LaunchAppHelper : PackageHelper() {

    private var mDirectionLocation: String? = null
    private val mCanLaunchThisTimeLocations = ArrayList<String>()

    //Create a delay before the Music App is launched and if enable launchPackage maps
    fun musicPlayerLaunch(context: Context, seconds: Int) {
        var seconds = seconds
        val pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context)
        seconds = seconds * 1000
        val handler = Handler()
        val runnable = Runnable { launchPackage(context, pkgName) }
        handler.postDelayed(runnable, seconds.toLong())
    }

    //Launch Maps or Waze with a delay
    fun launchMaps(context: Context, seconds: Int) {
        var seconds = seconds
        val canLaunchMapsNow = canMapsLaunchNow(context)

        if (canLaunchMapsNow) {
            seconds = seconds * 1000

            val handler = Handler()
            val runnable = Runnable {
                val mapAppName = BAPMPreferences.getMapsChoice(context)
                val isMapsRunning = isAppRunning(context, PackageHelper.MAPS)
                val canLaunchDirections = BAPMPreferences.getCanLaunchDirections(context) && !isMapsRunning
                if (canLaunchDirections) {
                    val data = getMapsChoiceUri(context)
                    launchPackage(context, mapAppName, data, Intent.ACTION_VIEW)
                } else {
                    determineIfLaunchWithDrivingMode(context, mapAppName, isMapsRunning)
                }
                Log.d(TAG, "delayLaunchmaps started")
            }
            handler.postDelayed(runnable, seconds.toLong())
        }
    }

    // If driving mode is enabled and map choice is set to Google Maps, launch Maps in Driving Mode
    private fun determineIfLaunchWithDrivingMode(context: Context, mapAppName: String, isMapsRunning: Boolean) {
        val canLaunchDrivingMode = BAPMPreferences.getLaunchMapsDrivingMode(context) &&
                mapAppName == PackageHelper.MAPS && (isMapsRunning.not())
        if (canLaunchDrivingMode) {
            Log.d(TAG, "LAUNCH DRIVING MODE")
            val data = Uri.parse("google.navigation:/?free=1&mode=d&entry=fnls")
            launchPackage(context, mapAppName, data, Intent.ACTION_VIEW)
        } else {
            launchPackage(context, mapAppName)
        }
    }

    private fun getMapsChoiceUri(context: Context): Uri {
        if (mDirectionLocation != null) {
            mDirectionLocation = if (mDirectionLocation == CUSTOM)
                BAPMPreferences.getCustomLocationName(context)
            else
                mDirectionLocation
        }

        val uri: Uri
        if (BAPMPreferences.getMapsChoice(context) == PackageHelper.WAZE) {
            val wazeUri = "waze://?favorite=$mDirectionLocation&navigate=yes"
            uri = Uri.parse(wazeUri)
        } else {
            val mapsUri = "google.navigation:q=" + mDirectionLocation!!
            uri = Uri.parse(mapsUri)
        }

        Log.d(TAG, "MAPS CHOICE URI:" + uri.toString())
        return uri
    }

    fun launchBAPMActivity(context: Context) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            val intentArray = arrayOfNulls<Intent>(2)
            intentArray[0] = Intent(context, MainActivity::class.java)
            intentArray[0]?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intentArray[1] = Intent(context, LaunchBAPMActivity::class.java)
            context.startActivities(intentArray)
        }
        handler.postDelayed(runnable, 750)
    }

    fun sendEverythingToBackground(context: Context) {
        val i = Intent(Intent.ACTION_MAIN)
        i.addCategory(Intent.CATEGORY_HOME)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(i)
    }

    fun canMapsLaunchNow(context: Context): Boolean {
        val isLaunchingWithDirections = BAPMPreferences.getCanLaunchDirections(context)
        val isUsingTimesToLaunch = BAPMPreferences.getUseTimesToLaunchMaps(context)
        val canLaunchDuringThisTime = canLaunchDuringThisTime(context)

        var canLaunchMapsNow = false
        if (isLaunchingWithDirections || isUsingTimesToLaunch) {
            if (canLaunchDuringThisTime) {
                for (location in mCanLaunchThisTimeLocations) {
                    canLaunchMapsNow = canLaunchOnThisDay(context, location)
                    if (canLaunchMapsNow) {
                        mDirectionLocation = location
                        break
                    }
                }
            }
            mCanLaunchThisTimeLocations.clear()
        } else {
            canLaunchMapsNow = true
        }

        return canLaunchMapsNow
    }

    fun canLaunchOnThisDay(context: Context, @DirectionLocations directionLocation: String): Boolean {
        val calendar = Calendar.getInstance()
        val today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK))
        var canLaunch = false

        when (directionLocation) {
            HOME -> canLaunch = BAPMPreferences.getHomeDaysToLaunchMaps(context)!!.contains(today)
            WORK -> canLaunch = BAPMPreferences.getWorkDaysToLaunchMaps(context)!!.contains(today)
            CUSTOM -> canLaunch = BAPMPreferences.getCustomDaysToLaunchMaps(context)!!.contains(today)
        }

        Log.d(TAG, "Day of the week: $today")
        Log.d(TAG, "Can Launch: $canLaunch")
        Log.d(TAG, "Direction Location: $directionLocation")

        return canLaunch
    }

    fun canLaunchDuringThisTime(context: Context): Boolean {
        val isUseLaunchTimeEnabled = BAPMPreferences.getUseTimesToLaunchMaps(context)
        if (isUseLaunchTimeEnabled) {

            val morningStartTime = BAPMPreferences.getMorningStartTime(context)
            val morningEndTime = BAPMPreferences.getMorningEndTime(context)

            val eveningStartTime = BAPMPreferences.getEveningStartTime(context)
            val eveningEndTime = BAPMPreferences.getEveningEndTime(context)

            val customStartTime = BAPMPreferences.getCustomStartTime(context)
            val customEndTime = BAPMPreferences.getCustomEndTime(context)

            val current24hrTime = TimeHelper.current24hrTime

            val timeHelperMorning = TimeHelper(morningStartTime, morningEndTime, current24hrTime)
            val canLaunchWork = timeHelperMorning.isWithinTimeSpan
            if (canLaunchWork) {
                mCanLaunchThisTimeLocations.add(WORK)
            }

            val timeHelperEvening = TimeHelper(eveningStartTime, eveningEndTime, current24hrTime)
            val canLaunchHome = timeHelperEvening.isWithinTimeSpan
            if (canLaunchHome) {
                mCanLaunchThisTimeLocations.add(HOME)
            }

            val timeHelperCustom = TimeHelper(customStartTime, customEndTime, current24hrTime)
            val canLaunchCustom = timeHelperCustom.isWithinTimeSpan
            if (canLaunchCustom) {
                mCanLaunchThisTimeLocations.add(CUSTOM)
            }

            return canLaunchWork || canLaunchHome || canLaunchCustom
        } else {
            return true
        }
    }

    fun launchWazeDirections(context: Context, location: String) {
        val handler = Handler()
        val runnable = Runnable {
            val uriString = "waze://?favorite=$location&navigate=yes"
            Log.d(TAG, "DIRECTIONS LOCATION: $uriString")
            val uri = Uri.parse(uriString)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            context.sendBroadcast(intent)
        }

        handler.postDelayed(runnable, 4000)
    }

    fun closeWazeOnDisconnect(context: Context) {
        val handler = Handler()
        val runnable = Runnable {
            val intent = Intent()
            intent.action = "Eliran_Close_Intent"
            context.sendBroadcast(intent)
        }

        handler.postDelayed(runnable, 2000)
    }

    companion object {
        @StringDef(HOME, WORK, CUSTOM)
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class DirectionLocations

        const val HOME = "Home"
        const val WORK = "Work"
        const val CUSTOM = "Custom"

        private const val TAG = "LaunchAppHelper"
    }
}

