package maderski.bluetoothautoplaymusic.bluetooth.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.bluetooth.receivers.BTStateChangedReceiver
import maderski.bluetoothautoplaymusic.common.AppScope
import maderski.bluetoothautoplaymusic.constants.ActionConstants.BT_STATE_CHANGED
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/6/17.
 */

class OnBTConnectService : Service(), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    private val btStateChangedReceiver: BTStateChangedReceiver by inject()
    private val preferencesHelper: PreferencesHelper by inject()

    private val binder = OnBTConnectBinder()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val message = getString(R.string.app_name)
        serviceManager.createServiceNotification(3451,
                getTitle(),
                message,
                this,
                ServiceManager.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceManager.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon,
                false)

        registerBTOnStateChangedReceiver()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = binder

    override fun onDestroy() {
        super.onDestroy()
        unregisterBTOnStateChangedReceiver()
        stopForeground(true)
    }

    private fun getTitle(): String {
        val waitTillPowerConnected = preferencesHelper.waitTillPowerConnected
        val resId = if (waitTillPowerConnected) R.string.connect_to_power_msg else R.string.connect_message
        return getString(resId)
    }

    private fun registerBTOnStateChangedReceiver() {
        Log.d(TAG, "START BT STATE CHANGED RECEIVER")
        val btStateChangedFilter = IntentFilter(BT_STATE_CHANGED)
        registerReceiver(btStateChangedReceiver, btStateChangedFilter)
    }

    private fun unregisterBTOnStateChangedReceiver() {
        try {
            Log.d(TAG, "STOP BT STATE CHANGED RECEIVER")
            unregisterReceiver(btStateChangedReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    inner class OnBTConnectBinder: Binder() {
        fun getService(): OnBTConnectService = this@OnBTConnectService
    }

    companion object {
        const val TAG = "OnBTConnectService"
    }
}
