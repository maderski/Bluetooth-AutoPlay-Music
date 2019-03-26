package maderski.bluetoothautoplaymusic.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

import java.util.ArrayList

import maderski.bluetoothautoplaymusic.R

/**
 * Created by Jason on 1/14/17.
 */

object BluetoothUtils {
    const val TAG = "BluetoothDeviceUtils"

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

    fun isBluetoothA2DPOnCompat(context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            audioManager.isBluetoothA2dpOn
        } else {
            audioManager.getDevices(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP).isNotEmpty()
        }
    }
}
