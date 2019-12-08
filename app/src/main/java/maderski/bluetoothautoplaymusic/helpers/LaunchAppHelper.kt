package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.enums.DirectionLocation
import maderski.bluetoothautoplaymusic.helpers.enums.DirectionLocation.*
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.*
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import maderski.bluetoothautoplaymusic.ui.activities.LaunchBAPMActivity
import maderski.bluetoothautoplaymusic.ui.activities.MainActivity
import java.util.*


/**
 * Created by Jason on 12/8/15.
 */
class LaunchAppHelper(
        private val context: Context,
        private val packageHelper: PackageHelper,
        private val preferencesHelper: PreferencesHelper
) {
    private val canLaunchThisTimeLocations = ArrayList<DirectionLocation>()

    private var directionLocation: DirectionLocation = NONE

    fun launchApp(packageName: String) = packageHelper.launchPackage(packageName)

    fun mapsLaunch() {
        val isMapsRunning = packageHelper.isAppRunning(MAPS.packageName)
        val canLaunch = canMapsLaunchNow()
        if (canLaunch && !isMapsRunning) {
            val canLaunchDirections = preferencesHelper.canLaunchDirections
            if (canLaunchDirections) {
                val mapAppName = preferencesHelper.mapAppName
                val data = getMapsChoiceUri()
                packageHelper.launchPackage(mapAppName, data, Intent.ACTION_VIEW)
            } else {
                determineHowToLaunchMaps()
            }
            Log.d(TAG, "Launch maps started")
        }
    }

    // Launch Maps or Waze with a delay
    fun launchMapsDelayed(seconds: Int) {
        val mills = seconds * 1000L

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            mapsLaunch()
        }
        handler.postDelayed(runnable, mills)
    }

    private fun determineHowToLaunchMaps() {
        val mapAppName = preferencesHelper.mapAppName
        val canLaunchDirections = preferencesHelper.canLaunchDirections
        if (canLaunchDirections && directionLocation != NONE) {
            val data = getMapsChoiceUri()
            packageHelper.launchPackage(mapAppName, data, Intent.ACTION_VIEW)
        } else {
            determineIfLaunchWithDrivingMode(mapAppName)
        }
    }

    // If driving mode is enabled and map choice is set to Google Maps, launch Maps in Driving Mode
    private fun determineIfLaunchWithDrivingMode(mapAppName: String) {
        val canLaunchDrivingMode = preferencesHelper.canLaunchDrivingMode
        if (canLaunchDrivingMode) {
            Log.d(TAG, "LAUNCH DRIVING MODE")
            val data = Uri.parse("google.navigation:/?free=1&mode=d&entry=fnls")
            packageHelper.launchPackage(mapAppName, data, Intent.ACTION_VIEW)
        } else {
            packageHelper.launchPackage(mapAppName)
        }
    }

    private fun getMapsChoiceUri(): Uri {
        val customLocationName = preferencesHelper.customLocationName
        val location = if (directionLocation == CUSTOM)
            customLocationName
        else
            directionLocation.location
        val mapAppChosen = preferencesHelper.mapAppChosen
        val uri = if (mapAppChosen == WAZE.packageName) {
            val wazeUri = "waze://?favorite=$location&navigate=yes"
            Uri.parse(wazeUri)
        } else {
            val mapsUri = "google.navigation:q=$location"
            Uri.parse(mapsUri)
        }

        Log.d(TAG, "MAPS CHOICE URI:$uri")
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

    fun launchDisconnectActivity() {
        val launchIntent = Intent(context, DisconnectActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(launchIntent)
    }

    fun canMapsLaunchNow(): Boolean {
        setDirectionLocation()
        val isLaunchingWithDirections = preferencesHelper.isLaunchingWithDirections
        val isUsingTimesToLaunch = preferencesHelper.isUsingTimesToLaunch
        return isLaunchingWithDirections || isUsingTimesToLaunch
    }

    private fun setDirectionLocation() {
        if (canLaunchDuringThisTime()) {
            directionLocation = canLaunchThisTimeLocations.find { directionLocation ->
                canLaunchOnThisDay(directionLocation)
            } ?: NONE
        }
        canLaunchThisTimeLocations.clear()
    }

    fun canLaunchOnThisDay(directionLocation: DirectionLocation): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK).toString()

        val daysToLaunchHome = preferencesHelper.daysToLaunchHome
        val daysToLaunchWork = preferencesHelper.daysToLaunchWork
        val daysToLaunchCustom = preferencesHelper.daysToLaunchCustom

        val canLaunch = when (directionLocation) {
            HOME -> daysToLaunchHome.contains(today)
            WORK -> daysToLaunchWork.contains(today)
            CUSTOM -> daysToLaunchCustom.contains(today)
            NONE -> false
        }

        Log.d(TAG, "Day of the week: $today")
        Log.d(TAG, "Can Launch: $canLaunch")
        Log.d(TAG, "Direction Location: $directionLocation")

        return canLaunch
    }

    fun canLaunchDuringThisTime(): Boolean {
        val isUseLaunchTimeEnabled = preferencesHelper.isUseLaunchTimeEnabled
        if (isUseLaunchTimeEnabled) {
            val current24hrTime = TimeHelper.current24hrTime
            val morningStartTime = preferencesHelper.morningStartTime
            val morningEndTime = preferencesHelper.morningEndTime
            val eveningStartTime = preferencesHelper.eveningStartTime
            val eveningEndTime = preferencesHelper.eveningEndTime
            val customStartTime = preferencesHelper.customStartTime
            val customEndTime = preferencesHelper.customEndTime

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

    companion object {
        private const val TAG = "LaunchAppHelper"
    }
}

