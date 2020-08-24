package maderski.bluetoothautoplaymusic.maps

import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import maderski.bluetoothautoplaymusic.common.AppScope
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.maps.MapsLaunchState.*

abstract class MapAppLauncher(
        private val launchHelper: LaunchHelper,
        private val preferencesHelper: PreferencesHelper
) : MapLaunchStateDeterminer by MapLaunchStateDeterminerImpl(preferencesHelper),
        DirectionLocationProvider by DirectionLocationProviderImpl(preferencesHelper),
        CoroutineScope by AppScope() {

    abstract fun getMapAppPkgName(): String

    abstract fun getMapAppDirectionUri(locationName: String): Uri

    abstract fun closeMaps()

    fun launchMaps() {
        val isMapsRunning = launchHelper.isAppRunning(getMapAppPkgName())
        val mapLaunchState = getMapsLaunchState(getMapAppPkgName(), isMapsRunning)
        Log.d(TAG, "Map Launch State: $mapLaunchState")
        when(mapLaunchState) {
            MAPS_IS_ALREADY_RUNNING -> onMapsIsAlreadyRunning()
            REGULAR_MAPS_LAUNCH -> onRegularMapsLaunch()
            DRIVING_MODE_MAPS_LAUNCH -> onDrivingModeMapsLaunch()
            USE_TIMES_MAPS_LAUNCH -> onUseTimesMapsLaunch()
            DRIVING_MODE_AND_USE_TIMES_MAPS_LAUNCH -> onDrivingModeAndUseTimesMapsLaunch()
            DIRECTIONS_AND_TIMES_MAPS_LAUNCH -> onDirectionsAndUseTimesMapsLaunch()
            EVERYTHING_ENABLED_MAPS_LAUNCH -> onEverythingEnabledMapsLaunch()
        }
    }

    protected fun sendBroadcast(intent: Intent) {
        launchHelper.sendBroadcast(intent)
    }

    // Maps is already running, so don't do anything
    private fun onMapsIsAlreadyRunning() {
        Log.d(TAG, "Maps is already running!")
    }

    // Just launch the map app
    private fun onRegularMapsLaunch() {
        Log.d(TAG, "Regular maps launch!")
        launchHelper.launchApp(getMapAppPkgName())
    }

    // Launch Google Maps in Driving Mode, only Google Maps Compatible
    private fun onDrivingModeMapsLaunch() {
        Log.d(TAG, "Driving mode maps launch!")
        launchGoogleMapsDrivingMode()
    }

    // Use TimeSpans to only launch maps in between specific times
    private fun onUseTimesMapsLaunch() {
        Log.d(TAG, "Use times maps launch!")
        onWithinTimeSpansPerformAction {
            launchHelper.launchApp(getMapAppPkgName())
        }
    }

    // Use TimeSpans to only launch maps in Driving Mode in between specific times
    private fun onDrivingModeAndUseTimesMapsLaunch() {
        Log.d(TAG, "Driving mode and use times maps launch!")
        onWithinTimeSpansPerformAction {
            launchWithDirectionsAndUseTimes()
        }
    }

    // Use TimeSpans to only launch maps with directions in between specific times
    private fun onDirectionsAndUseTimesMapsLaunch() {
        Log.d(TAG, "Directions and use times maps launch!")
        launchWithDirectionsAndUseTimes()
    }

    private fun onEverythingEnabledMapsLaunch() {
        Log.d(TAG, "Everything enabled maps launch!")
        launchWithDirectionsAndUseTimes()
    }

    private fun onWithinTimeSpansPerformAction(task: () -> Unit) {
        val directionLocation = getDirectionLocationForToday()
        if (directionLocation != DirectionLocation.NONE) {
            task()
        } else {
            Log.d(TAG, "Not with in time spans, will not launch maps!")
        }
    }

    private fun launchGoogleMapsDrivingMode() {
        val data = Uri.parse(DRIVING_MODE_URI)
        launchHelper.launchApp(getMapAppPkgName(), data, Intent.ACTION_VIEW)
    }

    private fun launchWithDirectionsAndUseTimes() {
        val packageName = getMapAppPkgName()
        val directionLocation = getDirectionLocationForToday()
        val locationName = getLocationName(directionLocation)
        val data = getMapAppDirectionUri(locationName)
        launchHelper.launchApp(packageName, data, Intent.ACTION_VIEW)
    }

    companion object {
        private const val TAG = "MapAppLauncher"
        private const val DRIVING_MODE_URI = "google.navigation:/?free=1&mode=d&entry=fnls"
    }
}