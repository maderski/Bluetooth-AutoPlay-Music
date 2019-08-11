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

/**
 * Created by Jason on 1/5/16.
 */
class BluetoothReceiver : BroadcastReceiver() {

    //On receive of Broadcast
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            if (btDevice != null) {
                val action: String = intent.action ?: ""
                Log.d(TAG, "ACTION: $action")

                val btDeviceName = btDevice.name ?: "None"
                val appContext = context.applicationContext
                val isASelectedBTDevice = BAPMPreferences.getBTDevices(appContext).contains(btDeviceName)
                val isAHeadphonesBTDevice = BAPMPreferences.getHeadphoneDevices(appContext).contains(btDeviceName)
                Log.d(TAG, "Device: " + btDeviceName +
                        "\nis SelectedBTDevice: " + java.lang.Boolean.toString(isASelectedBTDevice) +
                        "\nis A Headphone device: " + java.lang.Boolean.toString(isAHeadphonesBTDevice))

                if (isASelectedBTDevice || isAHeadphonesBTDevice) {
                    val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0)

                    if (isAHeadphonesBTDevice) {
                        val headphonesConnectHelper = HeadphonesConnectHelper(appContext, action, state)

                        headphonesConnectHelper.performActions()
                    } else if (action == BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
                        val bluetoothConnectHelper = BluetoothConnectHelper(appContext, btDeviceName)

                        bluetoothConnectHelper.a2dpActions(state)
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "BluetoothReceiver"
    }
}
