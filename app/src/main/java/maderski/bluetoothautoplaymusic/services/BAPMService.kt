package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.bluetooth.receivers.BTConnectionReceiver
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Jason on 1/5/16.
 */
class BAPMService : Service(), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    private val btConnectionReceiver: BTConnectionReceiver by inject()

    //Start the Bluetooth receiver as a service
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "started")
            Toast.makeText(this, "BAPMService started", Toast.LENGTH_LONG).show()
        }
        // Updating the service Notification will cause it to compact
        val title = getString(R.string.initializing)
        serviceManager.updateServiceNotification(title)

        // Start Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        registerReceiver(btConnectionReceiver, IntentFilter())

        // Bring service out of the foreground state
        stopForeground(true)

        // Stop the service from running
        stopSelf()

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val title = getString(R.string.initializing)
        val message = getString(R.string.app_name)
        serviceManager.createServiceNotification(ServiceManager.FOREGROUND_SERVICE_NOTIFICATION_ID,
                title,
                message,
                this,
                ServiceManager.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceManager.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon,
                false)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "stopped")
            Toast.makeText(this, "BAPMService stopped", Toast.LENGTH_LONG).show()
        }

        // Stop Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        unregisterReceiver(btConnectionReceiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        val TAG = "BAPMService"
    }
}
