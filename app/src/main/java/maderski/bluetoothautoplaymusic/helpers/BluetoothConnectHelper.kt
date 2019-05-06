package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Handler
import android.util.Log

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetoothactions.BTConnectActions
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.PlayMusicControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.services.BTDisconnectService
import maderski.bluetoothautoplaymusic.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/1/17.
 */

class BluetoothConnectHelper(private val context: Context, private val deviceName: String) {

    fun a2dpActions(state: Int) {
        val isHeadphones = BAPMDataPreferences.getIsAHeadphonesDevice(context)

        if (isHeadphones) {
            BAPMDataPreferences.setIsHeadphonesDevice(context, false)
        }

        when (state) {
            BluetoothProfile.STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
            BluetoothProfile.STATE_CONNECTED -> {
                Log.d(TAG, "A2DP CONNECTED")

                // Get Original volume
                val volumeControl = VolumeControl(context)
                volumeControl.saveOriginalVolume()
                Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)))

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
            Log.i(TAG, "Ran actionOnBTConnect: " + java.lang.Boolean.toString(BAPMDataPreferences.getRanActionsOnBtConnect(context)))
            Log.i(TAG, "LaunchNotifPresent: " + java.lang.Boolean.toString(BAPMDataPreferences.getLaunchNotifPresent(context)))
        }

        stopAdditionalServices()

        if (BAPMDataPreferences.getRanActionsOnBtConnect(context)) {
            PlayMusicControl.cancelCheckIfPlaying()
            ServiceUtils.startService(context, BTDisconnectService::class.java, BTDisconnectService.TAG)
        }

        if (BAPMPreferences.getWaitTillOffPhone(context) && BAPMDataPreferences.getLaunchNotifPresent(context)) {
            val bapmNotification = BAPMNotification(context)
            bapmNotification.removeBAPMMessage()
        }

        if (!BAPMDataPreferences.getRanActionsOnBtConnect(context)) {
            checkForWifiTurnOffDevice(false)
        }
    }

    private fun startAdditionalServices() {
        ServiceUtils.startService(context, OnBTConnectService::class.java, OnBTConnectService.TAG)
        ServiceUtils.startService(context, BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    private fun stopAdditionalServices() {
        val didNotLaunchBAPM = !BAPMDataPreferences.getRanActionsOnBtConnect(context)
        Log.d(TAG, "Did not launch BAPM: " + didNotLaunchBAPM.toString())

        if (didNotLaunchBAPM) {
            ServiceUtils.stopService(context, OnBTConnectService::class.java, OnBTConnectService.TAG)
        }

        ServiceUtils.stopService(context, BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    private fun checksBeforeLaunch() {
        val powerRequired = BAPMPreferences.getPowerConnected(context)

        val btConnectActions = BTConnectActions(context)

        if (!powerRequired) {
            btConnectActions.onBTConnect()
        }
    }

    private fun checkForWifiTurnOffDevice(isConnected: Boolean) {
        val turnOffWifiDevices = BAPMPreferences.getTurnWifiOffDevices(context)
        if (turnOffWifiDevices.isNotEmpty()) {
            if (turnOffWifiDevices.contains(deviceName)) {
                BAPMDataPreferences.setIsTurnOffWifiDevice(context, isConnected)
                Log.d(TAG, "TURN OFF WIFI DEVICE SET TO: " + java.lang.Boolean.toString(isConnected))
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothLaunchHelper"
    }
}
