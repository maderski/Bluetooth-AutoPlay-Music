package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothAdapter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import maderski.bluetoothautoplaymusic.bluetooth.models.BAPMDevice

import java.util.ArrayList

import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 1/14/17.
 */

class BluetoothDeviceHelper(
        private val systemServicesWrapper: SystemServicesWrapper
) {
    // List of bluetooth devices on the phone
    fun listOfBluetoothDevices(): List<BAPMDevice> {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        return if (bluetoothAdapter != null) {
            val pairedDevices = bluetoothAdapter.bondedDevices
            pairedDevices.map { BAPMDevice(it.name, it.address) }
        } else {
            emptyList()
        }
    }

    fun isBluetoothA2DPOnCompat(): Boolean {
        val audioManager = systemServicesWrapper.audioManager
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            audioManager.isBluetoothA2dpOn
        } else {
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any {
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
            }
        }
    }
}
