package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.bluetooth.models.BTDevice

/**
 * Created by Jason on 6/25/17.
 */

class A2DPHelper(private val callback: A2DPCallback) : BluetoothProfile.ServiceListener {

    interface A2DPCallback {
        fun connectedDeviceNames(deviceNames: Set<BTDevice>)
    }

    // Get List of currently connected bluetooth devices
    fun getConnectedA2DPDevices(context: Context) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.getProfileProxy(context, this, BluetoothProfile.A2DP)
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        val deviceNames = proxy.connectedDevices.map{ device ->
            BTDevice(device.name, device.address)
        }.toSet()
        Log.d(TAG, "CONNECTED DEVICES: $deviceNames")
        callback.connectedDeviceNames(deviceNames)
    }

    override fun onServiceDisconnected(profile: Int) {}

    companion object {
        private const val TAG = "A2DPHelper"
    }
}
