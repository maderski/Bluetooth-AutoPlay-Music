package maderski.bluetoothautoplaymusic.maps

import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper

class MapLauncherFactory(
        private val launchHelper: LaunchHelper,
        private val preferencesHelper: PreferencesHelper
) {
    fun getMapLauncher(mapApp: MapApps): MapAppLauncher =
            when (mapApp) {
                MapApps.MAPS -> GoogleMapsLauncher(launchHelper, preferencesHelper)
                MapApps.WAZE -> WazeLauncher(launchHelper, preferencesHelper)
            }
}