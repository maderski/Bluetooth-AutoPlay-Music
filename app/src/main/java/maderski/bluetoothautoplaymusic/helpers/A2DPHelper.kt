package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.collection.ArraySet
import android.util.Log

/**
 * Created by Jason on 6/25/17.
 */

class A2DPHelper(private val mCallbacks: A2DPCallbacks) : BluetoothProfile.ServiceListener {

    interface A2DPCallbacks {
        fun connectedDeviceNames(deviceNames: Set<String>)
    }

    // Get List of currently connected bluetooth devices
    fun getConnectedA2DPDevices(context: Context) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.getProfileProxy(context, this, BluetoothProfile.A2DP)
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        val deviceNames = androidx.collection.ArraySet<String>()
        val devices = proxy.connectedDevices
        for (device in devices) {
            Log.d(TAG, "CONNECTED DEVICE: " + device.name)
            deviceNames.add(device.name)
        }
        mCallbacks.connectedDeviceNames(deviceNames)
    }

    override fun onServiceDisconnected(profile: Int) {}

    companion object {
        private const val TAG = "A2DPHelper"
    }
}
