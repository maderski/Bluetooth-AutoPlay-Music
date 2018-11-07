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
import maderski.bluetoothautoplaymusic.BAPMNotification
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.services.BTDisconnectService
import maderski.bluetoothautoplaymusic.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/1/17.
 */

class BluetoothConnectHelper(private val mContext: Context, private val mDeviceName: String) {

    fun a2dpActions(state: Int) {
        val isHeadphones = BAPMDataPreferences.getIsAHeadphonesDevice(mContext)

        if (isHeadphones) {
            BAPMDataPreferences.setIsHeadphonesDevice(mContext, false)
        }

        when (state) {
            BluetoothProfile.STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
            BluetoothProfile.STATE_CONNECTED -> {
                Log.d(TAG, "A2DP CONNECTED")

                // Get Original volume
                val volumeControl = VolumeControl(mContext)
                volumeControl.saveOriginalVolume()
                Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(mContext)))

                checkForWifiTurnOffDevice(true)

                startAdditionalServices()

                Handler().postDelayed({
                    checksBeforeLaunch()

                    val firebaseHelper = FirebaseHelper(mContext)
                    firebaseHelper.connectViaA2DP(mDeviceName, true)
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
            Log.i(TAG, "Device disconnected: $mDeviceName")
            Log.i(TAG, "Ran actionOnBTConnect: " + java.lang.Boolean.toString(BAPMDataPreferences.getRanActionsOnBtConnect(mContext)))
            Log.i(TAG, "LaunchNotifPresent: " + java.lang.Boolean.toString(BAPMDataPreferences.getLaunchNotifPresent(mContext)))
        }

        stopAdditionalServices()

        if (BAPMDataPreferences.getRanActionsOnBtConnect(mContext)) {
            PlayMusicControl.cancelCheckIfPlaying()
            ServiceUtils.startService(mContext, BTDisconnectService::class.java, BTDisconnectService.TAG)
        }

        if (BAPMPreferences.getWaitTillOffPhone(mContext) && BAPMDataPreferences.getLaunchNotifPresent(mContext)) {
            val bapmNotification = BAPMNotification()
            bapmNotification.removeBAPMMessage(mContext)
        }

        if (!BAPMDataPreferences.getRanActionsOnBtConnect(mContext)) {
            checkForWifiTurnOffDevice(false)
        }
    }

    private fun startAdditionalServices() {
        ServiceUtils.startService(mContext, OnBTConnectService::class.java, OnBTConnectService.TAG)
        ServiceUtils.startService(mContext, BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    private fun stopAdditionalServices() {
        val didNotLaunchBAPM = !BAPMDataPreferences.getRanActionsOnBtConnect(mContext)
        Log.d(TAG, "Did not launch BAPM: " + didNotLaunchBAPM.toString())

        if (didNotLaunchBAPM) {
            ServiceUtils.stopService(mContext, OnBTConnectService::class.java, OnBTConnectService.TAG)
        }

        ServiceUtils.stopService(mContext, BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    private fun checksBeforeLaunch() {
        val powerRequired = BAPMPreferences.getPowerConnected(mContext)

        val btConnectActions = BTConnectActions(mContext)

        if (powerRequired) {
            if (PowerHelper.isPluggedIn(mContext)) {
                btConnectActions.onBTConnect()
            }
        } else {
            btConnectActions.onBTConnect()
        }
    }

    private fun checkForWifiTurnOffDevice(isConnected: Boolean) {
        val turnOffWifiDevices = BAPMPreferences.getTurnWifiOffDevices(mContext)
        if (turnOffWifiDevices.isNotEmpty()) {
            if (turnOffWifiDevices.contains(mDeviceName)) {
                BAPMDataPreferences.setIsTurnOffWifiDevice(mContext, isConnected)
                Log.d(TAG, "TURN OFF WIFI DEVICE SET TO: " + java.lang.Boolean.toString(isConnected))
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothLaunchHelper"
    }
}
