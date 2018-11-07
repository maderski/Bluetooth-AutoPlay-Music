package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.receivers.BluetoothReceiver
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 1/5/16.
 */
class BAPMService : Service() {

    private val mBluetoothReceiver = BluetoothReceiver()

    //Start the Bluetooth receiver as a service
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "started")
            Toast.makeText(this, "BAPMService started", Toast.LENGTH_LONG).show()
        }

        // Initalize and start Crashlytics
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }

        // Start Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        val filter = IntentFilter()
        registerReceiver(mBluetoothReceiver, filter)

        // Cancel the JobScheduler that was used to start the BTAPMService
        val jobScheduler: JobScheduler? = this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler?.let {
            it.cancelAll()
            Log.d(TAG, "BAPM JobService cancelled")
        }

        // Bring service out of the foreground state
        stopForeground(true)

        // Stop the service from running
        stopSelf()

        return Service.START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val title = getString(R.string.initializing)
        val message = getString(R.string.app_name)
        ServiceUtils.createServiceNotification(3455,
                title,
                message,
                this,
                ServiceUtils.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceUtils.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "stopped")
            Toast.makeText(this, "BAPMService stopped", Toast.LENGTH_LONG).show()
        }

        // Stop Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        unregisterReceiver(mBluetoothReceiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        val TAG = "BAPMService"
    }
}
