package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log

import maderski.bluetoothautoplaymusic.bluetoothactions.BTHeadphonesActions
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.ServiceUtils
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 11/4/17.
 */

class HeadphonesConnectHelper(
        private val mContext: Context,
        private val mAction: String,
        private val mState: Int
): KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val dataPreferences: BAPMDataPreferences by inject()

    fun performActions() {
        val doesRequireA2DP = preferences.getUseA2dpHeadphones()
        val btHeadphonesActions = BTHeadphonesActions(mContext)

        // Get Original volume
        val volumeControl = VolumeControl(mContext)
        volumeControl.saveOriginalVolume()
        Log.i(TAG, "Original Media Volume is: ${dataPreferences.getOriginalMediaVolume()}")

        if (doesRequireA2DP) {
            checkA2dpConnectionState(btHeadphonesActions)
        } else {
            checkBTConnectionState(btHeadphonesActions)
        }
    }

    private fun checkA2dpConnectionState(btHeadphonesActions: BTHeadphonesActions) {
        if (mAction == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
            when (mState) {
                BluetoothProfile.STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "A2DP CONNECTED")
                    btHeadphonesActions.connectActions()
                    ServiceUtils.startService(mContext, BTStateChangedService::class.java, BTStateChangedService.TAG)
                }
                BluetoothProfile.STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "A2DP DISCONNECTED")
                    btHeadphonesActions.disconnectActions()
                }
            }
        }
    }

    private fun checkBTConnectionState(btHeadphonesActions: BTHeadphonesActions) {
        when (mAction) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> btHeadphonesActions.connectActionsWithDelay()
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> btHeadphonesActions.disconnectActions()
        }
    }

    companion object {
        private const val TAG = "HeadphonesConnectHelper"
    }
}
