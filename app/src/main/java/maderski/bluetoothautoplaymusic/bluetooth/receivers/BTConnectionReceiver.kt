package maderski.bluetoothautoplaymusic.bluetooth.receivers

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.helpers.BTConnectionManager
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 1/5/16.
 */
class BTConnectionReceiver : BroadcastReceiver(), KoinComponent {
    private val preferencesHelper: PreferencesHelper by inject()
    private val btConnectionManager: BTConnectionManager by inject()
    private val firebaseHelper: FirebaseHelper by inject()

    //On receive of Broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val action = it.action
            if (action == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
                val btDevice = it.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val isASelectedBTDevice = preferencesHelper.isASelectedBTDevice(btDevice)
                if (isASelectedBTDevice) {
                    a2dpActions(it, btDevice)
                }
            }
        }
    }

    private fun a2dpActions(intent: Intent?, bluetoothDevice: BluetoothDevice?) {
        if (intent != null && bluetoothDevice != null) {
            val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0)
            when (state) {
                BluetoothProfile.STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "A2DP CONNECTED")
                    firebaseHelper.connectViaA2DP(bluetoothDevice.name, true)
                    btConnectionManager.startBTConnectService()
                }
                BluetoothProfile.STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "A2DP DISCONNECTED")
                    btConnectionManager.stopBTDisconnectService()
                }
            }
        }
    }

    companion object {
        private const val TAG = "BTConnectionReceiver"
    }
}
