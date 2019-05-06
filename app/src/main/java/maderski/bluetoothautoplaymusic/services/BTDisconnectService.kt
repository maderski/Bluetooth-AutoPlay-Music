package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

import maderski.bluetoothautoplaymusic.bluetoothactions.BTDisconnectActions
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 7/8/17.
 */

class BTDisconnectService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val btDisconnectActions = BTDisconnectActions(this)
        btDisconnectActions.actionsOnBTDisconnect()
        Log.d(TAG, "BT DISCONNECT SERVICE STARTED")

        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val title = getString(R.string.disconnect_message)
        val message = getString(R.string.app_name)
        ServiceUtils.createServiceNotification(3454,
                title,
                message,
                this,
                ServiceUtils.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceUtils.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon,
                false)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "BT DISCONNECT SERVICE STOPPED")

        stopForeground(true)
    }

    companion object {
        const val TAG = "BTDisconnectService"
    }
}
