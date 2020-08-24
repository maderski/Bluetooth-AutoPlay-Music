package maderski.bluetoothautoplaymusic.maps

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper

class WazeLauncher(
        launchHelper: LaunchHelper,
        preferencesHelper: PreferencesHelper
) : MapAppLauncher(launchHelper, preferencesHelper) {
    override fun getMapAppPkgName(): String = MapApps.WAZE.packageName

    override fun getMapAppDirectionUri(locationName: String): Uri =
            Uri.parse("waze://?favorite=$locationName&navigate=yes")

    override fun closeMaps() {
        launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                sendBroadcast(Intent().apply { action = "Eliran_Close_Intent" })
            }
        }
    }

}