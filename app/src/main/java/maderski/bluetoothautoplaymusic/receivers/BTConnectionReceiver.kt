package maderski.bluetoothautoplaymusic.receivers

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import maderski.bluetoothautoplaymusic.helpers.BluetoothConnectHelper
import maderski.bluetoothautoplaymusic.helpers.HeadphonesConnectHelper
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 1/5/16.
 */
class BTConnectionReceiver : BroadcastReceiver(), KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val bluetoothConnectHelper: BluetoothConnectHelper by inject()
    private val headphonesConnectHelper: HeadphonesConnectHelper by inject()

    //On receive of Broadcast
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val action = intent.action
            Log.d(TAG, "ACTION: $action")

            if (btDevice != null && action != null) {
                val btDeviceName = btDevice.name
                val appContext = context.applicationContext
                val isASelectedBTDevice = preferences.getBTDevices().contains(btDeviceName)
                val isAHeadphonesBTDevice = preferences.getHeadphoneDevices().contains(btDeviceName)
                Log.d(TAG, "Device: " + btDeviceName +
                        "\nis SelectedBTDevice: " + java.lang.Boolean.toString(isASelectedBTDevice) +
                        "\nis A Headphone device: " + java.lang.Boolean.toString(isAHeadphonesBTDevice))

                if (isASelectedBTDevice || isAHeadphonesBTDevice) {
                    val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0)
                    if (isAHeadphonesBTDevice) {
                        headphonesConnectHelper.performActions(action, state)
                    } else if (action == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
                        bluetoothConnectHelper.a2dpActions(appContext, state, btDeviceName)
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothReceiver"
    }
}
