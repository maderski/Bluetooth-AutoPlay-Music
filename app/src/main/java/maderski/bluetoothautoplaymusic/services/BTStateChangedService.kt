package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log

import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.receivers.BTStateChangedReceiver
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/6/17.
 */

class BTStateChangedService : Service() {

    private val mBtStateChangedReceiver = BTStateChangedReceiver()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "START BT STATE CHANGED RECEIVER")
        val btStateChangedFilter = IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED")
        registerReceiver(mBtStateChangedReceiver, btStateChangedFilter)
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val title = getString(R.string.listening_if_the_phones_BT_off)
            val message = getString(R.string.app_name)
            ServiceUtils.createServiceNotification(3452,
                    title,
                    message,
                    this,
                    ServiceUtils.CHANNEL_ID_FOREGROUND_SERVICE,
                    ServiceUtils.CHANNEL_NAME_FOREGROUND_SERVICE,
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
