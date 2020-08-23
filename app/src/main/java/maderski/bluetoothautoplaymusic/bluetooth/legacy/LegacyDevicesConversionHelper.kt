package maderski.bluetoothautoplaymusic.bluetooth.legacy

import android.bluetooth.BluetoothDevice
import maderski.bluetoothautoplaymusic.bluetooth.models.BAPMDevice
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

class LegacyDevicesConversionHelper (
        private val preferences: BAPMPreferences,
        private val legacyBluetoothSharedPrefs: LegacyBluetoothSharedPrefs
) {
    fun convertToBAPMDevices() {
        val legacyDevices = mutableSetOf<String>()
            legacyDevices.addAll(legacyBluetoothSharedPrefs.getBTDevicesLegacy())
            legacyDevices.addAll(legacyBluetoothSharedPrefs.getHeadphoneDevicesLegacy())
        if (legacyDevices.isNotEmpty()) {
            val bapmDevices = legacyDevices
                    .map { BAPMDevice(it, "") }
                    .toSet()
            preferences.setBAPMDevices(bapmDevices)
            legacyBluetoothSharedPrefs.removeAllLegacyDevices()
        }
    }

    fun convertLegacyDevice(bapmDevice: BAPMDevice, bluetoothDevice: BluetoothDevice): BAPMDevice {
        val updatedBapmDevice = bapmDevice.copy(macAddress = bluetoothDevice.address)
        updateBAPMDevice(updatedBapmDevice)
        return updatedBapmDevice
    }

    private fun updateBAPMDevice(bapmDevice: BAPMDevice) {
        val updatedDeviceSet = mutableSetOf<BAPMDevice>()
        updatedDeviceSet.add(bapmDevice)
        preferences.setBAPMDevices(updatedDeviceSet)
    }
}