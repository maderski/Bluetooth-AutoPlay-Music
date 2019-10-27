package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetooth.actions.BTConnectActions
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.services.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/1/17.
 */

class BluetoothConnectHelper(private val context: Context, private val deviceName: String): KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val dataPreferences: BAPMDataPreferences by inject()
    private val serviceManager: ServiceManager by inject()

    fun a2dpActions(state: Int) {
        val isHeadphones = dataPreferences.getIsAHeadphonesDevice()

        if (isHeadphones) {
            dataPreferences.setIsHeadphonesDevice(false)
        }

        when (state) {
            BluetoothProfile.STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
            BluetoothProfile.STATE_CONNECTED -> {
                Log.d(TAG, "A2DP CONNECTED")

                // Get Original volume
                val volumeControl = VolumeControl(context)
                volumeControl.saveOriginalVolume()
                Log.i(TAG, "Original Media Volume is: " + dataPreferences.getOriginalMediaVolume().toString())

                checkForWifiTurnOffDevice(true)

                startAdditionalServices()

                Handler().postDelayed({
                    checksBeforeLaunch()

                    val firebaseHelper = FirebaseHelper(context)
                    firebaseHelper.connectViaA2DP(deviceName, true)
                }, 500)
            }
            BluetoothProfile.STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
            BluetoothProfile.STATE_DISCONNECTED -> {
                Log.d(TAG, "A2DP DISCONNECTED")
                btDisconnectActions()
            }
        }
    }

    fun btDisconnectActions() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Device disconnected: $deviceName")
            Log.i(TAG, "LaunchNotifPresent: ${dataPreferences.getLaunchNotifPresent()}")
        }

        stopAdditionalServices()

        val disconnectIntent = Intent(context, DisconnectActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(disconnectIntent)

        if (preferences.getWaitTillOffPhone() && dataPreferences.getLaunchNotifPresent()) {
            val bapmNotification = BAPMNotification(context)
            bapmNotification.removeBAPMMessage()
        }

        checkForWifiTurnOffDevice(false)
    }

    private fun startAdditionalServices() {
        serviceManager.startService(OnBTConnectService::class.java, OnBTConnectService.TAG)
        //serviceManager.startService(BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    private fun stopAdditionalServices() {
        serviceManager.stopService(OnBTConnectService::class.java, OnBTConnectService.TAG)
        //serviceManager.stopService(BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    private fun checksBeforeLaunch() {
        val powerRequired = preferences.getPowerConnected()

        val btConnectActions = BTConnectActions(context)

        if (!powerRequired) {
            btConnectActions.onBTConnect()
        }
    }

    private fun checkForWifiTurnOffDevice(isConnected: Boolean) {
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
