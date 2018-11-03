package maderski.bluetoothautoplaymusic.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context

import java.util.ArrayList

import maderski.bluetoothautoplaymusic.R

/**
 * Created by Jason on 1/14/17.
 */

object BluetoothUtils {
    val TAG = "BluetoothDeviceUtils"

    // List of bluetooth devices on the phone
    fun listOfBluetoothDevices(context: Context): List<String> {
        val btDevices = ArrayList<String>()

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter != null) {
            val pairedDevices = bluetoothAdapter.bondedDevices

            for (bt in pairedDevices)
                btDevices.add(bt.name)
        } else {
            btDevices.add(0, context.getString(R.string.no_bluetooth_device))
        }

        return btDevices
    }

    fun enableDisabledBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
            }
        }
    }
}
