package maderski.bluetoothautoplaymusic.bluetooth.legacy

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMSharedPrefsAccess

class LegacyBluetoothSharedPrefs(private val sharedPrefsAccess: BAPMSharedPrefsAccess) {

    @Deprecated("Use getHeadphoneDevices Instead")
    fun getHeadphoneDevicesLegacy(): Set<String> = sharedPrefsAccess.getStringSet(HEADPHONE_DEVICES_LEGACY_KEY, setOf())

    @Deprecated("Use getBTDevices Instead")
    fun getBTDevicesLegacy(): Set<String> = sharedPrefsAccess.getStringSet(BTDEVICES_LEGACY_KEY, setOf())

    fun removeAllLegacyDevices() {
        sharedPrefsAccess.editor().remove(BTDEVICES_LEGACY_KEY)
        sharedPrefsAccess.editor().remove(HEADPHONE_DEVICES_LEGACY_KEY)
    }

    companion object {
        private const val HEADPHONE_DEVICES_LEGACY_KEY = "HeadphoneDevices"
        private const val BTDEVICES_LEGACY_KEY = "BTDevices"
    }
}