package maderski.bluetoothautoplaymusic.bluetooth.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.bluetooth.receivers.BTStateChangedReceiver
import maderski.bluetoothautoplaymusic.constants.ActionConstants.BT_STATE_CHANGED
import maderski.bluetoothautoplaymusic.constants.ActionConstants.POWER_CONNECTED
import maderski.bluetoothautoplaymusic.constants.ActionConstants.POWER_DISCONNECTED
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.helpers.PowerConnectedHelper
import maderski.bluetoothautoplaymusic.helpers.PowerHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.receivers.PowerConnectionReceiver
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.IllegalArgumentException

/**
 * Created by Jason on 6/6/17.
 */

class OnBTConnectService : Service(), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    private val powerHelper: PowerHelper by inject()
    private val powerConnectedHelper: PowerConnectedHelper by inject()
    private val btStateChangedReceiver: BTStateChangedReceiver by inject()
    private val powerConnectionReceiver: PowerConnectionReceiver by inject()
    private val preferencesHelper: PreferencesHelper by inject()
    private val volumeControl: VolumeControl by inject()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        volumeControl.saveOriginalVolume()

        val waitTillPowerConnected = preferencesHelper.waitTillPowerConnected

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

        registerBTOnStateChangedReceiver()

        if (waitTillPowerConnected) {
            val isPluggedIn = powerHelper.isPluggedIn()
            if (isPluggedIn) {
                powerConnectedHelper.performConnectActions()
            } else {
                registerPowerConnectionReceiver()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        val waitTillPowerConnected = preferencesHelper.waitTillPowerConnected
        if (waitTillPowerConnected) {
            unregisterPowerConnectionReceiver()
        }

        unregisterBTOnStateChangedReceiver()

        stopForeground(true)
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

    private fun registerPowerConnectionReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(POWER_CONNECTED)
        intentFilter.addAction(POWER_DISCONNECTED)

        registerReceiver(powerConnectionReceiver, intentFilter)
    }

    private fun unregisterPowerConnectionReceiver() {
        try {
            Log.d(TAG, "STOP POWER CONNECTION RECEIVER")
            unregisterReceiver(powerConnectionReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "OnBTConnectService"
    }
}
