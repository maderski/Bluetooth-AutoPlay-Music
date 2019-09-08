package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.serviceManager
import maderski.bluetoothautoplaymusic.workers.OnPowerConnectedWorker
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/6/17.
 */

class OnBTConnectService : Service(), KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val serviceManager: ServiceManager by inject()

    private var waitTillPowerConnected = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        waitTillPowerConnected = preferences.getPowerConnected()

        if (waitTillPowerConnected) {
            Log.d(TAG, "ENQUEUE POWER WORKER")
            // set must be charging constraint
            val constraints = Constraints.Builder()
                    .setRequiresCharging(true)
                    .build()
            // create work request
            val onPowerConnectedWorkRequest = OneTimeWorkRequestBuilder<OnPowerConnectedWorker>()
                    .addTag(OnPowerConnectedWorker.TAG)
                    .setConstraints(constraints)
                    .build()
            // enqueue work request
            WorkManager.getInstance().enqueue(onPowerConnectedWorkRequest)
        }

        val resId = if (waitTillPowerConnected) R.string.connect_to_power_msg else R.string.connect_message
        val title = getString(resId)
        val message = getString(R.string.app_name)
        serviceManager.createServiceNotification(3451,
                title,
                message,
                this,
                ServiceManager.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceManager.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon,
                false)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (waitTillPowerConnected) {
            WorkManager.getInstance().cancelAllWorkByTag(OnPowerConnectedWorker.TAG)
        }

        stopForeground(true)
    }

    companion object {
        const val TAG = "OnBTConnectService"
    }
}
