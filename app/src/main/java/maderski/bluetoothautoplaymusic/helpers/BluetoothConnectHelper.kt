package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothProfile.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.util.Log
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.bluetooth.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/1/17.
 */

class BluetoothConnectHelper(
        private val serviceManager: ServiceManager,
        private val firebaseHelper: FirebaseHelper,
        private val volumeControl: VolumeControl,
        private val btConnectActions: BTConnectActions,
        private val preferencesHelper: PreferencesHelper,
        private val bapmNotification: BAPMNotification
) : ServiceConnection {
    fun a2dpActions(context: Context, state: Int, deviceName: String) {
        val isHeadphones = preferencesHelper.isHeadphonesDevice
        if (isHeadphones) {
            preferencesHelper.isHeadphonesDevice = false
        }
        when (state) {
            STATE_CONNECTING -> {
                Log.d(TAG, "A2DP CONNECTING")
                // Get Original volume
                volumeControl.saveOriginalVolume()
                Log.i(TAG, "Original Media Volume is: " + preferencesHelper.originalMediaVolume.toString())
            }
            STATE_CONNECTED -> {
                Log.d(TAG, "A2DP CONNECTED")
                firebaseHelper.connectViaA2DP(deviceName, true)

                checkForWifiTurnOffDevice(state, deviceName)

                serviceManager.startService(OnBTConnectService::class.java, OnBTConnectService.TAG, this)
            }
            STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
            STATE_DISCONNECTED -> {
                Log.d(TAG, "A2DP DISCONNECTED")
                btDisconnectActions(context, deviceName, state)
            }
        }
    }

    private fun btDisconnectActions(context: Context, deviceName: String, state: Int) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Device disconnected: $deviceName")
            Log.i(TAG, "LaunchNotifPresent: ${preferencesHelper.isLaunchBTAPMNotifShowing}")
        }

        serviceManager.stopService(OnBTConnectService::class.java, OnBTConnectService.TAG)

        val disconnectIntent = Intent(context, DisconnectActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(disconnectIntent)

        if (preferencesHelper.waitTillOffPhone && preferencesHelper.isLaunchBTAPMNotifShowing) {
            bapmNotification.removeBAPMMessage()
        }

        checkForWifiTurnOffDevice(state, deviceName)
    }

    private fun checksBeforeLaunch() {
        val powerRequired = preferencesHelper.waitTillPowerConnected
        if (!powerRequired) {
            btConnectActions.onBTConnect()
        }
    }

    private fun checkForWifiTurnOffDevice(state: Int, deviceName: String) {
        val turnOffWifiDevices = preferencesHelper.turnWifiOffDevices
        if (turnOffWifiDevices.isNotEmpty()) {
            if (turnOffWifiDevices.contains(deviceName)) {
                val isConnected = state == STATE_CONNECTED
                preferencesHelper.isWifiOffDevice = isConnected
                Log.d(TAG, "TURN OFF WIFI DEVICE SET TO: $isConnected")
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        checksBeforeLaunch()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        // no-op
    }

    companion object {
        private const val TAG = "BluetoothLaunchHelper"
    }
}
