package maderski.bluetoothautoplaymusic.utils

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioManager
import android.util.Log

import java.util.Collections

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.helpers.A2DPHelper
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

/**
 * Created by Jason on 6/25/17.
 */

object ServiceRestartUtils {
    private val TAG = "ServiceRestartUtils"

    fun reHoldWakeLock(context: Context) {
        val shouldKeepScreenOn = BAPMPreferences.getKeepScreenON(context)

        if (shouldKeepScreenOn) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val ranBAPM = BAPMDataPreferences.getRanActionsOnBtConnect(context)
            val isConnectedToBT = audioManager.isBluetoothA2dpOn

            if (ranBAPM && isConnectedToBT) {
                // Rehold wakelock
                val screenONLock = ScreenONLock.getInstance()
                screenONLock.releaseWakeLock()
                screenONLock.enableWakeLock(context)

                // Check if Keyguard is locked, if so unlock it
                val isKeyguardLocked = (context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isKeyguardLocked
                val launchAppHelper = LaunchAppHelper()
                if (isKeyguardLocked) {
                    launchAppHelper.launchBAPMActivity(context)
                }

                // Log rehold wakelock event
                val firebaseHelper = FirebaseHelper(context)
                firebaseHelper.wakelockRehold()
            }
        }
    }

    fun restartAdditionServices(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val a2dpHelper = A2DPHelper(A2DPHelper.A2DPCallbacks { deviceNames ->
            val selectedBTDevices = BAPMPreferences.getBTDevices(context)

            val isBTConnected = audioManager.isBluetoothA2dpOn
            val isOnBTConnectServiceRunning = ServiceUtils.isServiceRunning(context, OnBTConnectService::class.java)
            val isBTStateChangedServiceRunning = ServiceUtils.isServiceRunning(context, BTStateChangedService::class.java)
            val isASelectedBTDevice = !Collections.disjoint(selectedBTDevices, deviceNames)
            Log.d(TAG, "CONNECTED DEVICE IS SELECTED: " + isASelectedBTDevice.toString()
                    + " " + deviceNames.size.toString())
            val shouldStartServices = isBTConnected && isASelectedBTDevice &&
                    !isBTStateChangedServiceRunning && !isOnBTConnectServiceRunning
            Log.d(TAG, "SHOULD START SERVICES: " + shouldStartServices.toString())
            if (shouldStartServices) {
                ServiceUtils.startService(context, OnBTConnectService::class.java, OnBTConnectService.TAG)
                ServiceUtils.startService(context, BTStateChangedService::class.java, BTStateChangedService.TAG)
            }
        })

        a2dpHelper.getConnectedA2DPDevices(context)

    }
}
