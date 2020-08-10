package maderski.bluetoothautoplaymusic.maps

import android.net.Uri
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper

class GoogleMapsLauncher(
        launchHelper: LaunchHelper,
        preferencesHelper: PreferencesHelper
) : MapAppLauncher(launchHelper, preferencesHelper) {
    override fun getMapAppPkgName(): String = MapApps.MAPS.packageName

    override fun getMapAppDirectionUri(locationName: String): Uri =
            Uri.parse("google.navigation:q=$locationName")

    // TODO: Figure out to stop directions in maps
    override fun closeMaps() {
        //no-op for Now
    }
}