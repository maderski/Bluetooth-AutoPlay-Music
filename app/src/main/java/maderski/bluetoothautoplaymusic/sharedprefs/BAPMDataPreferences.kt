package maderski.bluetoothautoplaymusic.sharedprefs

/**
 * Created by Jason on 8/6/16.
 */
class BAPMDataPreferences(private val sharedPrefsAccess: SharedPrefsAccess) {
    fun setIsTurnOffWifiDevice(isTurnOffWifiDevice: Boolean) = sharedPrefsAccess.putBoolean(IS_TURN_OFF_WIFI_DEVICE, isTurnOffWifiDevice)
    fun getIsTurnOffWifiDevice(): Boolean = sharedPrefsAccess.getBoolean(IS_TURN_OFF_WIFI_DEVICE, false)

    fun setIsHeadphonesDevice(isHeadphones: Boolean) = sharedPrefsAccess.putBoolean(IS_HEADPHONES_DEVICE, isHeadphones)
    fun getIsAHeadphonesDevice(): Boolean = sharedPrefsAccess.getBoolean(IS_HEADPHONES_DEVICE, false)

    fun setLaunchNotifPresent(enabled: Boolean) = sharedPrefsAccess.putBoolean(LAUNCH_NOTIF_PRESENT, enabled)
    fun getLaunchNotifPresent(): Boolean = sharedPrefsAccess.getBoolean(LAUNCH_NOTIF_PRESENT, false)

    fun setOriginalMediaVolume(volumeLevel: Int) = sharedPrefsAccess.putInt(ORIGINAL_MEDIA_VOLUME, volumeLevel)
    fun getOriginalMediaVolume(): Int = sharedPrefsAccess.getInt(ORIGINAL_MEDIA_VOLUME, 7)

    fun setCurrentRingerSet(ringerSetting: Int) = sharedPrefsAccess.putInt(CURRENT_RINGER_SET, ringerSetting)
    fun getCurrentRingerSet(): Int = sharedPrefsAccess.getInt(CURRENT_RINGER_SET, 2)

    companion object {
        const val MY_PREFS_NAME = "BAPMDataPreference"

        private const val CURRENT_RINGER_SET = "CurrentRingerSet"
        private const val ORIGINAL_MEDIA_VOLUME = "OriginalMediaVolume"
        private const val LAUNCH_NOTIF_PRESENT = "LaunchNotifPresent"
        private const val IS_HEADPHONES_DEVICE = "IsHeadphonesDevice"
        private const val IS_TURN_OFF_WIFI_DEVICE = "IsTurnOffWifiDevice"
    }
}
