package maderski.bluetoothautoplaymusic.bluetooth

import android.content.*
import android.os.IBinder
import android.util.Log
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.bluetooth.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.constants.ActionConstants
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.power.OnPowerConnectedAction
import maderski.bluetoothautoplaymusic.power.PowerInfo
import maderski.bluetoothautoplaymusic.power.PowerConnectionReceiver
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity

/**
 * Created by Jason on 6/1/17.
 */

class BTConnectionManager(
        private val appContext: Context,
        private val serviceManager: ServiceManager,
        private val preferencesHelper: PreferencesHelper,
        private val powerInfo: PowerInfo,
        private val powerConnectionReceiver: PowerConnectionReceiver,
        private val onPowerConnectedAction: OnPowerConnectedAction,
        private val btConnectActions: BTConnectActions
) : ServiceConnection {
    private var isPowerConnectionReceiverRegistered = false

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "OnBTConnectService CONNECTED!")
        val waitTillPowerConnected = preferencesHelper.waitTillPowerConnected
        if (waitTillPowerConnected) {
            checkIfPluggedIn()
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

    private fun checkIfPluggedIn() {
        val isPluggedIn = powerInfo.isPluggedIn()
        if (isPluggedIn) {
            onPowerConnectedAction.performBTConnectActions()
        } else {
            registerPowerConnectionReceiver()
        }
    }

    private fun registerPowerConnectionReceiver() {
        isPowerConnectionReceiverRegistered = true
        val intentFilter = IntentFilter().apply {
            addAction(ActionConstants.POWER_CONNECTED)
            addAction(ActionConstants.POWER_DISCONNECTED)
        }
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
