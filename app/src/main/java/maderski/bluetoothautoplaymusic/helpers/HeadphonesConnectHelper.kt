package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.util.Log

import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTHeadphonesActions
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 11/4/17.
 */

class HeadphonesConnectHelper: KoinComponent {
    private val preferencesHelper: PreferencesHelper by inject()
    private val btHeadphonesActions: BTHeadphonesActions by inject()
    private val volumeControl: VolumeControl by inject()

    fun performActions(action: String, state: Int) {
        val doesRequireA2DP = preferencesHelper.useA2dpHeadphones

        // Get Original volume
        volumeControl.saveOriginalVolume()
        Log.i(TAG, "Original Media Volume is: ${preferencesHelper.originalMediaVolume}")

        if (doesRequireA2DP) {
            checkA2dpConnectionState(action, state)
        } else {
            checkBTConnectionState(action)
        }
    }

    private fun checkA2dpConnectionState(action: String, state: Int) {
        if (action == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
            when (state) {
                BluetoothProfile.STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "A2DP CONNECTED")
                    btHeadphonesActions.connectActions()
                }
                BluetoothProfile.STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "A2DP DISCONNECTED")
                    btHeadphonesActions.disconnectActions()
                }
            }
        }
    }

    private fun checkBTConnectionState(action: String) {
        when (action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> btHeadphonesActions.connectActionsWithDelay()
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> btHeadphonesActions.disconnectActions()
        }
    }

    companion object {
        private const val TAG = "HeadphonesConnectHelper"
    }
}
