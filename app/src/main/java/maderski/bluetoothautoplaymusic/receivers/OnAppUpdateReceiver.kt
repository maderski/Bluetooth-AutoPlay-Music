package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import maderski.bluetoothautoplaymusic.services.jobservices.StartBAPMServiceJobService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/10/17.
 */

class OnAppUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action != null && action == Intent.ACTION_PACKAGE_REPLACED) {
                // Schedule Job to run on update
                ServiceUtils.scheduleJob(context, StartBAPMServiceJobService::class.java)

                // Sync newly separated Home Work checkboxes
                syncHomeWorkCheckboxes(context)
            }
        }
    }

    private fun syncHomeWorkCheckboxes(context: Context) {
        val hasRan = BAPMPreferences.getUpdateHomeWorkDaysSync(context)
        if (!hasRan) {
            val daysHomeWorkRan = BAPMPreferences.getHomeDaysToLaunchMaps(context) as Set<String>
            BAPMPreferences.setWorkDaysToLaunchMaps(context, daysHomeWorkRan)
            BAPMPreferences.setUpdateHomeWorkDaysSync(context, true)
            Log.d(TAG, "Work/Home Sync Complete")
        }
    }

    companion object {
        private const val TAG = "OnAppUpdateReceiver"
    }
}
