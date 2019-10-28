package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/1/17.
 */

class BluetoothConnectHelper: KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val dataPreferences: BAPMDataPreferences by inject()
    private val serviceManager: ServiceManager by inject()
    private val firebaseHelper: FirebaseHelper by inject()
    private val volumeControl: VolumeControl by inject()
    private val btConnectActions: BTConnectActions by inject()

    fun a2dpActions(context: Context, state: Int, deviceName: String) {
        val isHeadphones = dataPreferences.getIsAHeadphonesDevice()
        if (isHeadphones) {
            dataPreferences.setIsHeadphonesDevice(false)
        }

        when (state) {
            BluetoothProfile.STATE_CONNECTING -> {
                Log.d(TAG, "A2DP CONNECTING")
                // Get Original volume
                volumeControl.saveOriginalVolume()
                Log.i(TAG, "Original Media Volume is: " + dataPreferences.getOriginalMediaVolume().toString())
            }
            BluetoothProfile.STATE_CONNECTED -> {
                Log.d(TAG, "A2DP CONNECTED")

                checkForWifiTurnOffDevice(true, deviceName)

                serviceManager.startService(OnBTConnectService::class.java, OnBTConnectService.TAG)

                Handler().postDelayed({
                    checksBeforeLaunch()
                    firebaseHelper.connectViaA2DP(deviceName, true)
                }, 500)
            }
            BluetoothProfile.STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
            BluetoothProfile.STATE_DISCONNECTED -> {
                Log.d(TAG, "A2DP DISCONNECTED")
                btDisconnectActions(context, deviceName)
            }
        }
    }

    fun btDisconnectActions(context: Context, deviceName: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Device disconnected: $deviceName")
            Log.i(TAG, "LaunchNotifPresent: ${dataPreferences.getLaunchNotifPresent()}")
        }

        serviceManager.stopService(OnBTConnectService::class.java, OnBTConnectService.TAG)

        val disconnectIntent = Intent(context, DisconnectActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(disconnectIntent)

        if (preferences.getWaitTillOffPhone() && dataPreferences.getLaunchNotifPresent()) {
            val bapmNotification = BAPMNotification(context)
            bapmNotification.removeBAPMMessage()
        }

        checkForWifiTurnOffDevice(false, deviceName)
    }

    private fun checksBeforeLaunch() {
        val powerRequired = preferences.getPowerConnected()

        if (!powerRequired) {
            btConnectActions.onBTConnect()
        }
    }

    private fun checkForWifiTurnOffDevice(isConnected: Boolean, deviceName: String) {
        val turnOffWifiDevices = preferences.getTurnWifiOffDevices()
        if (turnOffWifiDevices.isNotEmpty()) {
            if (turnOffWifiDevices.contains(deviceName)) {
                dataPreferences.setIsTurnOffWifiDevice(isConnected)
                Log.d(TAG, "TURN OFF WIFI DEVICE SET TO: $isConnected")
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothLaunchHelper"
    }
}
