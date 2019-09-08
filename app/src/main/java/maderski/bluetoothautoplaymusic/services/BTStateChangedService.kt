package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log

import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.receivers.BTStateChangedReceiver
import maderski.bluetoothautoplaymusic.utils.serviceManager
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/6/17.
 */

class BTStateChangedService : Service(), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    private val mBtStateChangedReceiver = BTStateChangedReceiver()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "START BT STATE CHANGED RECEIVER")
        val btStateChangedFilter = IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED")
        registerReceiver(mBtStateChangedReceiver, btStateChangedFilter)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val title = getString(R.string.listening_if_the_phones_BT_off)
            val message = getString(R.string.app_name)
            serviceManager.createServiceNotification(3452,
                    title,
                    message,
                    this,
                    ServiceManager.CHANNEL_ID_FOREGROUND_SERVICE,
                    ServiceManager.CHANNEL_NAME_FOREGROUND_SERVICE,
                    R.drawable.ic_notif_icon,
                    false)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop receivers
        Log.d(TAG, "STOP BT STATE CHANGED RECEIVER")
        unregisterReceiver(mBtStateChangedReceiver)

        stopForeground(true)
    }

    companion object {
        const val TAG = "BTStateChangedService"
    }
}
