package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import maderski.bluetoothautoplaymusic.ui.activities.LaunchBAPMActivity
import maderski.bluetoothautoplaymusic.ui.activities.MainActivity


/**
 * Created by Jason on 12/8/15.
 */
class LaunchHelper(
        private val context: Context,
        private val packageHelper: PackageHelper
) {
    fun launchApp(packageName: String) = packageHelper.launchPackage(packageName)

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

    fun isAbleToLaunch(packageName: String) = packageHelper.isPackageOnPhone(packageName)
}

