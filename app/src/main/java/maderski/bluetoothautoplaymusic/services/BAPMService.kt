package maderski.bluetoothautoplaymusic.services

import android.app.Service
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
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 1/5/16.
 */
class BAPMService : Service(), KoinComponent {
    private val serviceManager: ServiceManager by inject()

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
        serviceManager.createServiceNotification(3455,
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
        unregisterReceiver(mBluetoothReceiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        val TAG = "BAPMService"
    }
}
