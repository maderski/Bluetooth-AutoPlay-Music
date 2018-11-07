package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

import maderski.bluetoothautoplaymusic.BAPMNotification
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.receivers.CustomReceiver
import maderski.bluetoothautoplaymusic.receivers.PowerReceiver
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/6/17.
 */

class OnBTConnectService : Service() {

    private val mPowerReceiver = PowerReceiver()
    private val mCustomReceiver = CustomReceiver()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val waitTillPowerConnected = BAPMPreferences.getPowerConnected(this)
        val waitTillOffPhone = BAPMPreferences.getWaitTillOffPhone(this)

        // Start receivers
        if (waitTillPowerConnected) {
            Log.d(TAG, "START POWER RECEIVER")
            val powerFilter = IntentFilter("android.intent.action.ACTION_POWER_CONNECTED")
            registerReceiver(mPowerReceiver, powerFilter)
        }

        if (waitTillOffPhone || waitTillPowerConnected) {
            Log.d(TAG, "START CUSTOM RECEIVER")
            val customFilter = IntentFilter()
            customFilter.addAction("maderski.bluetoothautoplaymusic.pluggedinlaunch")
            customFilter.addAction("maderski.bluetoothautoplaymusic.offtelephonelaunch")
            registerReceiver(mCustomReceiver, customFilter)
        }

        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val waitTillPowerConnected = BAPMPreferences.getPowerConnected(this)

        val title = getString(if (waitTillPowerConnected) R.string.connect_to_power_msg else R.string.connect_message)
        val message = getString(R.string.app_name)
        ServiceUtils.createServiceNotification(3451,
                title,
                message,
                this,
                ServiceUtils.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceUtils.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop receivers
        if (BAPMPreferences.getPowerConnected(this)) {
            Log.d(TAG, "STOP POWER RECEIVER")
            unregisterReceiver(mPowerReceiver)
        }

        if (BAPMPreferences.getWaitTillOffPhone(this) || BAPMPreferences.getPowerConnected(this)) {
            Log.d(TAG, "STOP CUSTOM RECEIVER")
            unregisterReceiver(mCustomReceiver)
        }

        stopForeground(true)
    }

    companion object {
        const val TAG = "OnBTConnectService"
    }
}
