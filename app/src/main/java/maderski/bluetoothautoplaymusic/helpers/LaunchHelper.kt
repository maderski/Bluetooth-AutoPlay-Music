package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import maderski.bluetoothautoplaymusic.common.AppScope
import maderski.bluetoothautoplaymusic.maps.MapApps
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import maderski.bluetoothautoplaymusic.ui.activities.LaunchBAPMActivity
import maderski.bluetoothautoplaymusic.ui.activities.MainActivity
import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper


/**
 * Created by Jason on 12/8/15.
 */
class LaunchHelper(
        private val context: Context,
        private val packageHelper: PackageHelper,
        private val stringResourceWrapper: StringResourceWrapper
) : CoroutineScope by AppScope() {
    fun launchApp(packageName: String)  {
        val launchIntent = packageHelper.getLaunchIntent(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            showUnableToLaunchToast(packageName)
        }
    }

    fun launchApp(packageName: String, data: Uri, action: String) {
        val launchIntent = packageHelper.getLaunchIntent(packageName)
        if (launchIntent != null) {
            launchIntent.action = action
            launchIntent.data = data
            context.startActivity(launchIntent)
        } else {
            showUnableToLaunchToast(packageName)
        }
    }

    fun launchBAPMActivity() {
        launch(Dispatchers.Main) {
            delay(750)
            val intentArray = arrayOfNulls<Intent>(2)
            intentArray[0] = Intent(context, MainActivity::class.java)
            intentArray[0]?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intentArray[1] = Intent(context, LaunchBAPMActivity::class.java)
            context.startActivities(intentArray)
        }
    }

    fun launchDisconnectActivity() {
        val launchIntent = Intent(context, DisconnectActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(launchIntent)
    }

    fun isAbleToLaunch(packageName: String) = packageHelper.isPackageOnPhone(packageName)

    private fun showUnableToLaunchToast(packageName: String) {
        val toastMsg = when(packageName) {
            MapApps.MAPS.packageName -> stringResourceWrapper.unableToLaunchMaps
            MapApps.WAZE.packageName -> stringResourceWrapper.unableToLaunchWaze
            else -> stringResourceWrapper.unableToLaunchMediaPlayer
        }
        Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
    }

    fun sendBroadcast(intent: Intent) = context.sendBroadcast(intent)

    // Is app running on phone
    fun isAppRunning(packageName: String): Boolean {
        val activityManager = packageHelper.getActivityManager()
        val processInfos = activityManager.runningAppProcesses
        return processInfos.any { processInfo -> processInfo.processName == packageName }
    }
}

