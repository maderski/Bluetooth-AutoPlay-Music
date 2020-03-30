package maderski.bluetoothautoplaymusic.launchers

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import maderski.bluetoothautoplaymusic.helpers.enums.DirectionLocation
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps
import java.util.*

class MapAppLauncher(
        private val launchHelper: LaunchHelper,
        private val preferencesHelper: PreferencesHelper
) {
    fun mapsLaunch() {
        val isMapsRunning = launchHelper.isAppRunning(MapApps.MAPS.packageName)
        val canLaunch = canMapsLaunchNow()
        if (canLaunch && !isMapsRunning) {
            val canLaunchDirections = preferencesHelper.canLaunchDirections
            if (canLaunchDirections) {
                val mapAppName = preferencesHelper.mapAppName
                val directionLocations = getDirectionLocations()
                val data = getMapsChoiceUri(directionLocations)
                launchHelper.launchApp(mapAppName, data, Intent.ACTION_VIEW)
            } else {
                determineHowToLaunchMaps()
            }
            Log.d(TAG, "Launch maps started")
        }
    }

    private fun determineHowToLaunchMaps() {
        val mapAppName = preferencesHelper.mapAppName
        val directionLocations = getDirectionLocations()
        val canLaunchDirections = preferencesHelper.canLaunchDirections && canLaunchDuringThisTime(directionLocations)
        val directionLocation = directionLocations.find { directionLocation ->
            canLaunchOnThisDay(directionLocation)
        } ?: DirectionLocation.NONE
        if (canLaunchDirections && directionLocation != DirectionLocation.NONE) {
            val data = getMapsChoiceUri(directionLocations)
            launchHelper.launchApp(mapAppName, data, Intent.ACTION_VIEW)
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
            launchHelper.launchApp(mapAppName, data, Intent.ACTION_VIEW)
        } else {
            launchHelper.launchApp(mapAppName)
        }
    }

    private fun getMapsChoiceUri(directionLocations: List<DirectionLocation>): Uri {
        val customLocationName = preferencesHelper.customLocationName
        val directionLocation = directionLocations.find { directionLocation ->
            canLaunchOnThisDay(directionLocation)
        } ?: DirectionLocation.NONE
        val location = if (directionLocation == DirectionLocation.CUSTOM)
            customLocationName
        else
            directionLocation.location
        val mapAppChosen = preferencesHelper.mapAppChosen
        val uri = if (mapAppChosen == MapApps.WAZE.packageName) {
            val wazeUri = "waze://?favorite=$location&navigate=yes"
            Uri.parse(wazeUri)
        } else {
            val mapsUri = "google.navigation:q=$location"
            Uri.parse(mapsUri)
        }

        Log.d(TAG, "MAPS CHOICE URI:$uri")
        return uri
    }

    fun canMapsLaunchNow(): Boolean {
        val isLaunchingWithDirections = preferencesHelper.isLaunchingWithDirections
        val isUsingTimesToLaunch = preferencesHelper.isUsingTimesToLaunch
        return isLaunchingWithDirections || isUsingTimesToLaunch
    }

    fun canLaunchOnThisDay(directionLocation: DirectionLocation): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK).toString()

        val daysToLaunchHome = preferencesHelper.daysToLaunchHome
        val daysToLaunchWork = preferencesHelper.daysToLaunchWork
        val daysToLaunchCustom = preferencesHelper.daysToLaunchCustom

        val canLaunch = when (directionLocation) {
            DirectionLocation.HOME -> daysToLaunchHome.contains(today)
            DirectionLocation.WORK -> daysToLaunchWork.contains(today)
            DirectionLocation.CUSTOM -> daysToLaunchCustom.contains(today)
            DirectionLocation.NONE -> false
        }

        Log.d(TAG, "Day of the week: $today")
        Log.d(TAG, "Can Launch: $canLaunch")
        Log.d(TAG, "Direction Location: $directionLocation")

        return canLaunch
    }

    private fun canLaunchDuringMorningTimes(): Boolean {
        val morningStartTime = preferencesHelper.morningStartTime
        val morningEndTime = preferencesHelper.morningEndTime
        return isWithinTimeSpan(morningStartTime, morningEndTime)
    }

    private fun canLaunchDuringEveningTimes(): Boolean {
        val eveningStartTime = preferencesHelper.eveningStartTime
        val eveningEndTime = preferencesHelper.eveningEndTime
        return isWithinTimeSpan(eveningStartTime, eveningEndTime)
    }

    private fun canLaunchDuringCustomTimes(): Boolean {
        val customStartTime = preferencesHelper.customStartTime
        val customEndTime = preferencesHelper.customEndTime
        return isWithinTimeSpan(customStartTime, customEndTime)
    }

    private fun isWithinTimeSpan(startTime: Int, endTime: Int): Boolean {
        val current24hrTime = TimeHelper.current24hrTime
        val timeHelper = TimeHelper(startTime, endTime, current24hrTime)
        return timeHelper.isWithinTimeSpan
    }

    private fun getDirectionLocations(): List<DirectionLocation> {
        val canLaunchWork = canLaunchDuringMorningTimes()
        val canLaunchHome = canLaunchDuringEveningTimes()
        val canLaunchCustom = canLaunchDuringCustomTimes()
        val directionLocations = mutableListOf<DirectionLocation>()
        when {
            canLaunchWork -> directionLocations.add(DirectionLocation.WORK)
            canLaunchHome -> directionLocations.add(DirectionLocation.HOME)
            canLaunchCustom -> directionLocations.add(DirectionLocation.CUSTOM)
        }
        return directionLocations
    }

    fun canLaunchDuringThisTime(directionLocations: List<DirectionLocation>): Boolean {
        val isUseLaunchTimeEnabled = preferencesHelper.isUseLaunchTimeEnabled
        return if (isUseLaunchTimeEnabled) {
            directionLocations.isNotEmpty()
        } else {
            true
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
            launchHelper.sendBroadcast(intent)
        }

        handler.postDelayed(runnable, 4000)
    }

    fun closeWazeOnDisconnect() {
        val handler = Handler()
        val runnable = Runnable {
            val intent = Intent()
            intent.action = "Eliran_Close_Intent"
            launchHelper.sendBroadcast(intent)
        }

        handler.postDelayed(runnable, 2000)
    }

    companion object {
        const val TAG = "MapAppLauncher"
    }
}