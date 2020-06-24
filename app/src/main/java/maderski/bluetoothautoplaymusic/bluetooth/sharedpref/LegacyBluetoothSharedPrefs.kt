package maderski.bluetoothautoplaymusic.bluetooth.sharedpref

import maderski.bluetoothautoplaymusic.helpers.SerializationHelper
import maderski.bluetoothautoplaymusic.sharedprefs.SharedPrefsAccess

class LegacyBluetoothSharedPrefsImpl(private val sharedPrefsAccess: SharedPrefsAccess, serializationHelper: SerializationHelper) {

    @Deprecated("Use getHeadphoneDevices Instead")
    fun getHeadphoneDevicesLegacy(): Set<String> = sharedPrefsAccess.getStringSet(HEADPHONE_DEVICES_LEGACY_KEY, setOf())

    @Deprecated("Use getBTDevices Instead")
    fun getBTDevicesLegacy(): Set<String> = sharedPrefsAccess.getStringSet(BTDEVICES_LEGACY_KEY, setOf())

    companion object {
        private const val HEADPHONE_DEVICES_LEGACY_KEY = "HeadphoneDevices"
        private const val BTDEVICES_LEGACY_KEY = "BTDevices"
    }
}