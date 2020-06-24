package maderski.bluetoothautoplaymusic.sharedprefs

import maderski.bluetoothautoplaymusic.bluetooth.models.BAPMDevice
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.MAPS
import maderski.bluetoothautoplaymusic.helpers.enums.MediaPlayers.GOOGLE_PLAY_MUSIC


/**
 * Created by Jason on 1/5/16.
 *
 * Save and read program settings using this class
 */
class BAPMPreferences(private val sharedPrefsAccess: SharedPrefsAccess) {
    private val launchDays = mutableSetOf("1", "2", "3", "4", "5", "6", "7")

    fun setAskedFirebaseOptIn(hasAsked: Boolean) = sharedPrefsAccess.putBoolean(ASKED_FIREBASE_OPT_IN, hasAsked)
    fun getAskedFirebaseOptIn(): Boolean = sharedPrefsAccess.getBoolean(ASKED_FIREBASE_OPT_IN, false)

    fun setUseFirebaseAnalytics(canUseFirebase: Boolean) = sharedPrefsAccess.putBoolean(USE_FIREBASE_ANALYTICS, canUseFirebase)
    fun getUseFirebaseAnalytics(): Boolean = sharedPrefsAccess.getBoolean(USE_FIREBASE_ANALYTICS, true)

    fun setUpdateHomeWorkDaysSync(hasRan: Boolean) = sharedPrefsAccess.putBoolean(UPDATE_HOME_WORK_DAYS_SYNC, hasRan)
    fun getUpdateHomeWorkDaysSync(): Boolean = sharedPrefsAccess.getBoolean(UPDATE_HOME_WORK_DAYS_SYNC, false)

    fun setUseA2dpHeadphones(enable: Boolean) = sharedPrefsAccess.putBoolean(USE_A2DP_HEADPHONES, enable)
    fun getUseA2dpHeadphones(): Boolean = sharedPrefsAccess.getBoolean(USE_A2DP_HEADPHONES, false)

    fun setUsePriorityMode(enable: Boolean) = sharedPrefsAccess.putBoolean(USE_PRIORITY_MODE, enable)
    fun getUsePriorityMode(): Boolean = sharedPrefsAccess.getBoolean(USE_PRIORITY_MODE, false)

    fun setLaunchMapsDrivingMode(enable: Boolean) = sharedPrefsAccess.putBoolean(LAUNCH_MAPS_DRIVING_MODE, enable)
    fun getLaunchMapsDrivingMode(): Boolean = sharedPrefsAccess.getBoolean(LAUNCH_MAPS_DRIVING_MODE, false)

    fun setRestoreNotificationVolume(enable: Boolean) = sharedPrefsAccess.putBoolean(RESTORE_NOTIFICATION_VOLUME_KEY, enable)
    fun getRestoreNotificationVolume(): Boolean = sharedPrefsAccess.getBoolean(RESTORE_NOTIFICATION_VOLUME_KEY, true)

    fun setCanLaunchDirections(enable: Boolean) = sharedPrefsAccess.putBoolean(LAUNCH_DIRECTIONS, enable)
    fun getCanLaunchDirections(): Boolean = sharedPrefsAccess.getBoolean(LAUNCH_DIRECTIONS, false)

    fun setShowNotification(enable: Boolean) = sharedPrefsAccess.putBoolean(SHOW_NOTIFICATION, enable)
    fun getShowNotification(): Boolean = sharedPrefsAccess.getBoolean(SHOW_NOTIFICATION, false)

    fun setMorningStartTime(startTime: Int) = sharedPrefsAccess.putInt(MORNING_START_TIME, startTime)
    fun getMorningStartTime(): Int = sharedPrefsAccess.getInt(MORNING_START_TIME, 700)

    fun setMorningEndTime(endTime: Int) = sharedPrefsAccess.putInt(MORNING_END_TIME, endTime)
    fun getMorningEndTime(): Int = sharedPrefsAccess.getInt(MORNING_END_TIME, 1000)

    fun setEveningStartTime(startTime: Int) = sharedPrefsAccess.putInt(EVENING_START_TIME, startTime)
    fun getEveningStartTime(): Int = sharedPrefsAccess.getInt(EVENING_START_TIME, 1600)

    fun setEveningEndTime(endTime: Int) = sharedPrefsAccess.putInt(EVENING_END_TIME, endTime)
    fun getEveningEndTime(): Int = sharedPrefsAccess.getInt(EVENING_END_TIME, 1900)

    fun setCustomStartTime(startTime: Int) = sharedPrefsAccess.putInt(CUSTOM_START_TIME, startTime)
    fun getCustomStartTime(): Int = sharedPrefsAccess.getInt(CUSTOM_START_TIME, 1100)

    fun setCustomEndTime(endTime: Int) = sharedPrefsAccess.putInt(CUSTOM_END_TIME, endTime)
    fun getCustomEndTime(): Int = sharedPrefsAccess.getInt(CUSTOM_END_TIME, 1300)

    fun setCustomLocationName(locationName: String) = sharedPrefsAccess.putString(CUSTOM_LOCATION_NAME, locationName)
    fun getCustomLocationName(): String = sharedPrefsAccess.getString(CUSTOM_LOCATION_NAME, "")

    fun setUseTimesToLaunchMaps(enabled: Boolean) = sharedPrefsAccess.putBoolean(USE_TIMES_TO_LAUNCH_MAPS, enabled)
    fun getUseTimesToLaunchMaps(): Boolean = sharedPrefsAccess.getBoolean(USE_TIMES_TO_LAUNCH_MAPS, false)

    fun setCloseWazeOnDisconnect(enabled: Boolean) = sharedPrefsAccess.putBoolean(CLOSE_WAZE_ON_DISCONNECT, enabled)
    fun getCloseWazeOnDisconnect(): Boolean = sharedPrefsAccess.getBoolean(CLOSE_WAZE_ON_DISCONNECT, true)

    fun setUserSetMaxVolume(volume: Int) = sharedPrefsAccess.putInt(USER_SET_MAX_VOLUME_KEY, volume)
    fun getUserSetMaxVolume(deviceMaxVolume: Int): Int = sharedPrefsAccess.getInt(USER_SET_MAX_VOLUME_KEY, deviceMaxVolume)

    fun setBrightTime(time: Int) = sharedPrefsAccess.putInt(BRIGHT_TIME_KEY, time)
    fun getBrightTime(): Int = sharedPrefsAccess.getInt(BRIGHT_TIME_KEY, 700)

    fun setDimTime(time: Int) = sharedPrefsAccess.putInt(DIM_TIME_KEY, time)
    fun getDimTime(): Int = sharedPrefsAccess.getInt(DIM_TIME_KEY, 2000)

    fun setCustomDaysToLaunchMaps(customDays: MutableSet<String>) = sharedPrefsAccess.putStringSet(CUSTOM_DAYS_TO_LAUNCH_MAPS_KEY, customDays)
    fun getCustomDaysToLaunchMaps(): Set<String>? = sharedPrefsAccess.getStringSet(CUSTOM_DAYS_TO_LAUNCH_MAPS_KEY, mutableSetOf())

    fun setWorkDaysToLaunchMaps(_stringSet: MutableSet<String>) = sharedPrefsAccess.putStringSet(WORK_DAYS_TO_LAUNCH_MAPS_KEY, _stringSet)
    fun getWorkDaysToLaunchMaps(): Set<String>? = sharedPrefsAccess.getStringSet(WORK_DAYS_TO_LAUNCH_MAPS_KEY, launchDays)

    fun setHomeDaysToLaunchMaps(_stringSet: MutableSet<String>) = sharedPrefsAccess.putStringSet(DAYS_TO_LAUNCH_MAPS_KEY, _stringSet)
    fun getHomeDaysToLaunchMaps(): Set<String>? = sharedPrefsAccess.getStringSet(DAYS_TO_LAUNCH_MAPS_KEY, launchDays)

    fun setAutoBrightness(enabled: Boolean) = sharedPrefsAccess.putBoolean(AUTO_BRIGHTNESS_KEY, enabled)
    fun getAutoBrightness(): Boolean = sharedPrefsAccess.getBoolean(AUTO_BRIGHTNESS_KEY, false)

    fun setLaunchGoogleMaps(enabled: Boolean) = sharedPrefsAccess.putBoolean(LAUNCH_MAPS_KEY, enabled)
    fun getLaunchGoogleMaps(): Boolean = sharedPrefsAccess.getBoolean(LAUNCH_MAPS_KEY, false)

    fun setKeepScreenON(enabled: Boolean) = sharedPrefsAccess.putBoolean(KEEP_SCREEN_ON_KEY, enabled)
    fun getKeepScreenON(): Boolean = sharedPrefsAccess.getBoolean(KEEP_SCREEN_ON_KEY, false)

    fun setPriorityMode(enabled: Boolean) = sharedPrefsAccess.putBoolean(PRIORITY_MODE_KEY, enabled)
    fun getPriorityMode(): Boolean = sharedPrefsAccess.getBoolean(PRIORITY_MODE_KEY, false)

    fun setMaxVolume(enabled: Boolean) = sharedPrefsAccess.putBoolean(MAX_VOLUME_KEY, enabled)
    fun getMaxVolume(): Boolean = sharedPrefsAccess.getBoolean(MAX_VOLUME_KEY, false)

    fun setLaunchMusicPlayer(enabled: Boolean) = sharedPrefsAccess.putBoolean(LAUNCH_MUSIC_PLAYER_KEY, enabled)
    fun getLaunchMusicPlayer(): Boolean = sharedPrefsAccess.getBoolean(LAUNCH_MUSIC_PLAYER_KEY, false)

    fun setPkgSelectedMusicPlayer(packageName: String) = sharedPrefsAccess.putString(PKG_SELECTED_MUSIC_PLAYER_KEY, packageName)
    fun getPkgSelectedMusicPlayer(): String = sharedPrefsAccess.getString(PKG_SELECTED_MUSIC_PLAYER_KEY, GOOGLE_PLAY_MUSIC.packageName)

    fun setUnlockScreen(enabled: Boolean) = sharedPrefsAccess.putBoolean(UNLOCK_SCREEN_KEY, enabled)
    fun getUnlockScreen(): Boolean = sharedPrefsAccess.getBoolean(UNLOCK_SCREEN_KEY, false)

    fun setMapsChoice(SelectedMapsApp: String) = sharedPrefsAccess.putString(MAPS_CHOICE_KEY, SelectedMapsApp)
    fun getMapsChoice(): String = sharedPrefsAccess.getString(MAPS_CHOICE_KEY, MAPS.packageName)

    fun setAutoplayMusic(enabled: Boolean) = sharedPrefsAccess.putBoolean(AUTOPLAY_MUSIC_KEY, enabled)
    fun getAutoPlayMusic(): Boolean = sharedPrefsAccess.getBoolean(AUTOPLAY_MUSIC_KEY, true)

    fun setPowerConnected(enabled: Boolean) = sharedPrefsAccess.putBoolean(POWER_CONNECTED_KEY, enabled)
    fun getPowerConnected(): Boolean = sharedPrefsAccess.getBoolean(POWER_CONNECTED_KEY, false)

    fun setSendToBackground(enabled: Boolean) = sharedPrefsAccess.putBoolean(SEND_TO_BACKGROUND_KEY, enabled)
    fun getSendToBackground(): Boolean = sharedPrefsAccess.getBoolean(SEND_TO_BACKGROUND_KEY, false)

    fun setWaitTillOffPhone(enabled: Boolean) = sharedPrefsAccess.putBoolean(WAIT_TILL_OFF_PHONE_KEY, enabled)
    fun getWaitTillOffPhone(): Boolean = sharedPrefsAccess.getBoolean(WAIT_TILL_OFF_PHONE_KEY, true)

    fun setBAPMDevices(bapmDevices: Set<BAPMDevice>) = sharedPrefsAccess.putBAPMDeviceSet(BTDEVICES_KEY, bapmDevices)
    fun getBAPMDevices(): Set<BAPMDevice> = sharedPrefsAccess.getBAPMDeviceSet(BTDEVICES_KEY, setOf())

    companion object {
        const val MY_PREFS_NAME = "BAPMPreference"

        // Keys for enabling/disabling things
        private const val LAUNCH_MAPS_KEY = "googleMaps"
        private const val KEEP_SCREEN_ON_KEY = "keepScreenON"
        private const val PRIORITY_MODE_KEY = "priorityMode"
        private const val MAX_VOLUME_KEY = "maxVolume"
        private const val LAUNCH_MUSIC_PLAYER_KEY = "launchMusic"
        private const val PKG_SELECTED_MUSIC_PLAYER_KEY = "PkgSelectedMusicPlayer"
        private const val UNLOCK_SCREEN_KEY = "UnlockScreen"
        private const val MAPS_CHOICE_KEY = "MapsChoice"
        private const val AUTOPLAY_MUSIC_KEY = "AutoPlayMusic"
        private const val POWER_CONNECTED_KEY = "PowerConnected"
        private const val SEND_TO_BACKGROUND_KEY = "SendToBackground"
        private const val WAIT_TILL_OFF_PHONE_KEY = "WaitTillOffPhone"
        private const val AUTO_BRIGHTNESS_KEY = "AutoBrightness"
        private const val DAYS_TO_LAUNCH_MAPS_KEY = "DaysToLaunchMaps"
        private const val WORK_DAYS_TO_LAUNCH_MAPS_KEY = "WorkDaysToLaunchMaps"
        private const val CUSTOM_DAYS_TO_LAUNCH_MAPS_KEY = "CustomDaysToLaunchMaps"
        private const val DIM_TIME_KEY = "DimTime"
        private const val BRIGHT_TIME_KEY = "BrightTime"
        private const val HEADPHONE_PREFERRED_VOLUME_KEY = "HeadphonePreferredVolumeKey"
        private const val USER_SET_MAX_VOLUME_KEY = "UserSetMaxVolumeKey"
        private const val CLOSE_WAZE_ON_DISCONNECT = "CloseWazeOnDisconnect"
        private const val TURN_WIFI_OFF_DEVICES = "TurnWifiOffDevices"
        private const val USE_TIMES_TO_LAUNCH_MAPS = "UseTimesToLaunchMaps"
        private const val MORNING_START_TIME = "MorningStartTime"
        private const val MORNING_END_TIME = "MorningEndTime"
        private const val EVENING_START_TIME = "EveningStartTime"
        private const val EVENING_END_TIME = "EveningEndTime"
        private const val CUSTOM_START_TIME = "CustomStartTime"
        private const val CUSTOM_END_TIME = "CustomEndTime"
        private const val CUSTOM_LOCATION_NAME = "CustomLocationName"
        private const val SHOW_NOTIFICATION = "ShowNotification"
        private const val LAUNCH_DIRECTIONS = "LaunchDirections"
        private const val WIFI_USE_MAP_TIME_SPANS = "WifiUseMapTimeSpans"
        private const val RESTORE_NOTIFICATION_VOLUME_KEY = "RestoreNotificationVoluemKey"
        private const val LAUNCH_MAPS_DRIVING_MODE = "LaunchMapsDrivingMode"
        private const val USE_PRIORITY_MODE = "UsePriorityMode"
        private const val USE_A2DP_HEADPHONES = "UseA2DPHeadphones"
        private const val UPDATE_HOME_WORK_DAYS_SYNC = "updateHomeDaysSync"
        private const val USE_FIREBASE_ANALYTICS = "UseFirebaseAnalytics"
        private const val ASKED_FIREBASE_OPT_IN = "AskedFirebaseOptIn"
        // Bluetooth devices
        private const val HEADPHONE_DEVICES_KEY = "HeadphoneDevicesKey"
        private const val BTDEVICES_KEY = "BluetoothDevicesKey"
    }
}
