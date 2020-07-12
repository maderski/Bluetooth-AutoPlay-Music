package maderski.bluetoothautoplaymusic.bluetooth.receivers

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import maderski.bluetoothautoplaymusic.helpers.BluetoothConnectionManager
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 1/5/16.
 */
class BTConnectionReceiver : BroadcastReceiver(), KoinComponent {
    private val preferencesHelper: PreferencesHelper by inject()
    private val bluetoothConnectionManager: BluetoothConnectionManager by inject()

    //On receive of Broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val action = it.action
            if (action == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
                val btDevice = it.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val isASelectedBTDevice = preferencesHelper.isASelectedBTDevice(btDevice)
                if (isASelectedBTDevice) {
                    bluetoothConnectionManager.a2dpActions(it, btDevice)
                }
            }
        }
    }
}
