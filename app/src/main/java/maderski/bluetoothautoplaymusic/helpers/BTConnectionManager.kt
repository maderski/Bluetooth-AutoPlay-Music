package maderski.bluetoothautoplaymusic.helpers

import android.content.*
import android.os.IBinder
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.bluetooth.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.constants.ActionConstants
import maderski.bluetoothautoplaymusic.receivers.PowerConnectionReceiver
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity

/**
 * Created by Jason on 6/1/17.
 */

class BTConnectionManager(
        private val appContext: Context,
        private val serviceManager: ServiceManager,
        private val preferencesHelper: PreferencesHelper,
        private val powerHelper: PowerHelper,
        private val powerConnectionReceiver: PowerConnectionReceiver,
        private val powerConnectedHelper: PowerConnectedHelper,
        private val btConnectActions: BTConnectActions
) : ServiceConnection {
    private var isPowerConnectionReceiverRegistered = false

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "OnBTConnectService CONNECTED!")
        val waitTillPowerConnected = preferencesHelper.waitTillPowerConnected
        if (waitTillPowerConnected) {
            val isPluggedIn = powerHelper.isPluggedIn()
            if (isPluggedIn) {
                powerConnectedHelper.performConnectActions()
            } else {
                registerPowerConnectionReceiver()
            }
        } else {
            btConnectActions.onBTConnect()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        if (isPowerConnectionReceiverRegistered) {
            unregisterPowerConnectionReceiver()
        }
    }

    fun startBTConnectService() {
        serviceManager.startService(OnBTConnectService::class.java, OnBTConnectService.TAG, this)
    }

    fun stopBTDisconnectService() {
        serviceManager.stopService(OnBTConnectService::class.java, OnBTConnectService.TAG)
        val disconnectIntent = Intent(appContext, DisconnectActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        appContext.startActivity(disconnectIntent)
    }

    private fun registerPowerConnectionReceiver() {
        isPowerConnectionReceiverRegistered = true
        val intentFilter = IntentFilter()
        intentFilter.addAction(ActionConstants.POWER_CONNECTED)
        intentFilter.addAction(ActionConstants.POWER_DISCONNECTED)

        appContext.registerReceiver(powerConnectionReceiver, intentFilter)
    }

    private fun unregisterPowerConnectionReceiver() {
        try {
            Log.d(TAG, "STOP POWER CONNECTION RECEIVER")
            appContext.unregisterReceiver(powerConnectionReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        isPowerConnectionReceiverRegistered = false
    }

    companion object {
        private const val TAG = "BluetoothLaunchHelper"
    }
}
