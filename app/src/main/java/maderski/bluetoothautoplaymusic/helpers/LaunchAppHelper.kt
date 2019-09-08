package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper.DirectionLocation.*
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MapApps.MAPS
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MapApps.WAZE
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.ui.activities.LaunchBAPMActivity
import maderski.bluetoothautoplaymusic.ui.activities.MainActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*


/**
 * Created by Jason on 12/8/15.
 */
class LaunchAppHelper(
        private val context: Context,
        private val packageHelper: PackageHelper,
        preferences: BAPMPreferences
) {

    private val canLaunchThisTimeLocations = ArrayList<DirectionLocation>()

    private var directionLocation: DirectionLocation = NONE

    // SharePrefs Info
    private val mapAppChosen = preferences.getMapsChoice()
    private val customLocationName = preferences.getCustomLocationName()
    private val mapAppName = preferences.getMapsChoice()
    private val canLaunchDirections = preferences.getCanLaunchDirections()
    private val canLaunchDrivingMode = preferences.getLaunchMapsDrivingMode() &&
            mapAppName == MAPS.packageName
    private val isLaunchingWithDirections = preferences.getCanLaunchDirections()
    private val isUsingTimesToLaunch = preferences.getUseTimesToLaunchMaps()

    private val morningStartTime = preferences.getMorningStartTime()
    private val morningEndTime = preferences.getMorningEndTime()

    private val eveningStartTime = preferences.getEveningStartTime()
    private val eveningEndTime = preferences.getEveningEndTime()

    private val customStartTime = preferences.getCustomStartTime()
    private val customEndTime = preferences.getCustomEndTime()

    private val isUseLaunchTimeEnabled = preferences.getUseTimesToLaunchMaps()

    private val musicPlayerPkgName = preferences.getPkgSelectedMusicPlayer()

    private val daysToLaunchHome = preferences.getHomeDaysToLaunchMaps()
    private val daysToLaunchWork = preferences.getWorkDaysToLaunchMaps()
    private val daysToLaunchCustom = preferences.getCustomDaysToLaunchMaps()

    fun launchApp(packageName: String) = packageHelper.launchPackage(packageName)

    //Create a delay before the Music App is launched and if enable launchPackage maps
    fun musicPlayerLaunch(seconds: Int) {
        packageHelper.launchPackage(musicPlayerPkgName)
    }

    //Launch Maps or Waze with a delay
    fun launchMaps(seconds: Int) {
        val canLaunchMapsNow = canMapsLaunchNow()

        if (canLaunchMapsNow) {
            val mills = seconds * 1000L

            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                if (canLaunchDirections) {
                    val data = getMapsChoiceUri()
                    packageHelper.launchPackage(mapAppName, data, Intent.ACTION_VIEW)
                } else {
                    determineIfLaunchWithDrivingMode(mapAppName)
                }
                Log.d(TAG, "delayLaunchmaps started")

            }
            handler.postDelayed(runnable, mills)
        }
    }

    private fun determineHowToLaunchMaps() {
        val isMapsRunning = packageHelper.isAppRunning(MAPS.packageName)
        if (canLaunchDirections && !isMapsRunning && directionLocation != NONE) {
            val data = getMapsChoiceUri()
            packageHelper.launchPackage(mapAppName, data, Intent.ACTION_VIEW)
        } else {
            determineIfLaunchWithDrivingMode(mapAppName)
        }
        Log.d(TAG, "delayLaunchmaps started")
    }

    // If driving mode is enabled and map choice is set to Google Maps, launch Maps in Driving Mode
    private fun determineIfLaunchWithDrivingMode(mapAppName: String) {
        if (canLaunchDrivingMode) {
            Log.d(TAG, "LAUNCH DRIVING MODE")
            val data = Uri.parse("google.navigation:/?free=1&mode=d&entry=fnls")
            packageHelper.launchPackage(mapAppName, data, Intent.ACTION_VIEW)
        } else {
            packageHelper.launchPackage(mapAppName)
        }
    }

    private fun getMapsChoiceUri(): Uri {
        val location = if (directionLocation == CUSTOM)
            customLocationName
        else
            directionLocation.location

        val uri: Uri
        if (mapAppChosen == WAZE.packageName) {
            val wazeUri = "waze://?favorite=$location&navigate=yes"
            uri = Uri.parse(wazeUri)
        } else {
            val mapsUri = "google.navigation:q=" + location
            uri = Uri.parse(mapsUri)
        }

        Log.d(TAG, "MAPS CHOICE URI:" + uri.toString())
        return uri
    }

    fun launchBAPMActivity() {
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

    fun canMapsLaunchNow(): Boolean {
        val canLaunchDuringThisTime = canLaunchDuringThisTime()

        var canLaunchMapsNow = false
        if (isLaunchingWithDirections || isUsingTimesToLaunch) {
            if (canLaunchDuringThisTime) {
                directionLocation = canLaunchThisTimeLocations.find { directionLocation ->
                    canLaunchOnThisDay(directionLocation)
                } ?: NONE
            }
            canLaunchThisTimeLocations.clear()
        } else {
            canLaunchMapsNow = true
        }

        return canLaunchMapsNow
    }

    fun canLaunchOnThisDay(directionLocation: DirectionLocation): Boolean {
        val calendar = Calendar.getInstance()
        val today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK))
        var canLaunch = false

        when (directionLocation) {
            HOME -> canLaunch = daysToLaunchHome?.contains(today) ?: false
            WORK -> canLaunch = daysToLaunchWork?.contains(today) ?: false
            CUSTOM -> canLaunch = daysToLaunchCustom?.contains(today) ?: false
            NONE -> false
        }

        Log.d(TAG, "Day of the week: $today")
        Log.d(TAG, "Can Launch: $canLaunch")
        Log.d(TAG, "Direction Location: $directionLocation")

        return canLaunch
    }

    fun canLaunchDuringThisTime(): Boolean {
        if (isUseLaunchTimeEnabled) {
            val current24hrTime = TimeHelper.current24hrTime

            val timeHelperMorning = TimeHelper(morningStartTime, morningEndTime, current24hrTime)
            val canLaunchWork = timeHelperMorning.isWithinTimeSpan
            if (canLaunchWork) {
                canLaunchThisTimeLocations.add(WORK)
            }

            val timeHelperEvening = TimeHelper(eveningStartTime, eveningEndTime, current24hrTime)
            val canLaunchHome = timeHelperEvening.isWithinTimeSpan
            if (canLaunchHome) {
                canLaunchThisTimeLocations.add(HOME)
            }

            val timeHelperCustom = TimeHelper(customStartTime, customEndTime, current24hrTime)
            val canLaunchCustom = timeHelperCustom.isWithinTimeSpan
            if (canLaunchCustom) {
                canLaunchThisTimeLocations.add(CUSTOM)
            }

            return canLaunchWork || canLaunchHome || canLaunchCustom
        } else {
            return true
        }
    }

    fun launchWazeDirections(location: String) {
        val handler = Handler()
        val runnable = Runnable {
            val uriString = "waze://?favorite=$location&navigate=yes"
            Log.d(TAG, "DIRECTIONS LOCATION: $uriString")
            val uri = Uri.parse(uriString)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            packageHelper.sendBroadcast(intent)
        }

        handler.postDelayed(runnable, 4000)
    }

    fun closeWazeOnDisconnect() {
        val handler = Handler()
        val runnable = Runnable {
            val intent = Intent()
            intent.action = "Eliran_Close_Intent"
            packageHelper.sendBroadcast(intent)
        }

        handler.postDelayed(runnable, 2000)
    }

    fun isAbleToLaunch(packageName: String) = packageHelper.isPackageOnPhone(packageName)

    enum class DirectionLocation(val location: String) {
        NONE("None"),
        HOME("Home"),
        WORK("Work"),
        CUSTOM("Custom")
    }

    companion object {
        private const val TAG = "LaunchAppHelper"
    }
}

