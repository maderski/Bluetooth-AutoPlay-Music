package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothAdapter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

import java.util.ArrayList

import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 1/14/17.
 */

class BluetoothDeviceHelper(
        private val systemServicesWrapper: SystemServicesWrapper,
        private val stringResourceWrapper: StringResourceWrapper
) {
    // List of bluetooth devices on the phone
    fun listOfBluetoothDevices(): List<String> {
        val btDevices = ArrayList<String>()

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter != null) {
            val pairedDevices = bluetoothAdapter.bondedDevices

            for (bt in pairedDevices)
                btDevices.add(bt.name)
        } else {
            btDevices.add(0, stringResourceWrapper.noBluetoothDevice)
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

    fun isBluetoothA2DPOnCompat(): Boolean {
        val audioManager = systemServicesWrapper.audioManager
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            audioManager.isBluetoothA2dpOn
        } else {
            audioManager.getDevices(AudioManager.GET_DEVICES_ALL).any {
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
            }
        }
    }
}
