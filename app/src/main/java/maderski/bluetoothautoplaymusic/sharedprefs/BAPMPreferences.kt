package maderski.bluetoothautoplaymusic.sharedprefs

import android.content.Context
import android.content.SharedPreferences

import java.util.HashSet

import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.controls.VolumeControl

/**
 * Created by Jason on 1/5/16.
 *
 * Save and read program settings using this class
 */
object BAPMPreferences {
    private const val MY_PREFS_NAME = "BAPMPreference"

    //Keys for enabling/disabling things
    private const val LAUNCH_MAPS_KEY = "googleMaps"
    private const val KEEP_SCREEN_ON_KEY = "keepScreenON"
    private const val PRIORITY_MODE_KEY = "priorityMode"
    private const val MAX_VOLUME_KEY = "maxVolume"
    private const val LAUNCH_MUSIC_PLAYER_KEY = "launchMusic"
    private const val PKG_SELECTED_MUSIC_PLAYER_KEY = "PkgSelectedMusicPlayer"
    private const val UNLOCK_SCREEN_KEY = "UnlockScreen"
    private const val BTDEVICES_KEY = "BTDevices"
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
    private const val HEADPHONE_DEVICES_KEY = "HeadphoneDevices"
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

    private val launchDays = mutableSetOf("1", "2", "3", "4", "5", "6", "7")

    fun setAskedFirebaseOptIn(context: Context, hasAsked: Boolean) = editor(context).putBoolean(ASKED_FIREBASE_OPT_IN, hasAsked).apply()
    fun getAskedFirebaseOptIn(context: Context): Boolean = reader(context).getBoolean(ASKED_FIREBASE_OPT_IN, false)

    fun setUseFirebaseAnalytics(context: Context, canUseFirebase: Boolean) = editor(context).putBoolean(USE_FIREBASE_ANALYTICS, canUseFirebase).apply()
    fun getUseFirebaseAnalytics(context: Context): Boolean = reader(context).getBoolean(USE_FIREBASE_ANALYTICS, true)

    fun setUpdateHomeWorkDaysSync(context: Context, hasRan: Boolean) = editor(context).putBoolean(UPDATE_HOME_WORK_DAYS_SYNC, hasRan).apply()
    fun getUpdateHomeWorkDaysSync(context: Context): Boolean = reader(context).getBoolean(UPDATE_HOME_WORK_DAYS_SYNC, false)

    fun setUseA2dpHeadphones(context: Context, enable: Boolean) = editor(context).putBoolean(USE_A2DP_HEADPHONES, enable).apply()
    fun getUseA2dpHeadphones(context: Context): Boolean = reader(context).getBoolean(USE_A2DP_HEADPHONES, false)

    fun setUsePriorityMode(context: Context, enable: Boolean) = editor(context).putBoolean(USE_PRIORITY_MODE, enable).apply()
    fun getUsePriorityMode(context: Context): Boolean = reader(context).getBoolean(USE_PRIORITY_MODE, false)

    fun setLaunchMapsDrivingMode(context: Context, enable: Boolean) = editor(context).putBoolean(LAUNCH_MAPS_DRIVING_MODE, enable).apply()
    fun getLaunchMapsDrivingMode(context: Context): Boolean = reader(context).getBoolean(LAUNCH_MAPS_DRIVING_MODE, false)

    fun setRestoreNotificationVolume(context: Context, enable: Boolean) = editor(context).putBoolean(RESTORE_NOTIFICATION_VOLUME_KEY, enable).apply()
    fun getRestoreNotificationVolume(context: Context): Boolean = reader(context).getBoolean(RESTORE_NOTIFICATION_VOLUME_KEY, true)

    fun setWifiUseMapTimeSpans(context: Context, enable: Boolean) = editor(context).putBoolean(WIFI_USE_MAP_TIME_SPANS, enable).apply()
    fun getWifiUseMapTimeSpans(context: Context): Boolean = reader(context).getBoolean(WIFI_USE_MAP_TIME_SPANS, false)

    fun setCanLaunchDirections(context: Context, enable: Boolean) = editor(context).putBoolean(LAUNCH_DIRECTIONS, enable).apply()
    fun getCanLaunchDirections(context: Context): Boolean = reader(context).getBoolean(LAUNCH_DIRECTIONS, false)

    fun setShowNotification(context: Context, enable: Boolean) = editor(context).putBoolean(SHOW_NOTIFICATION, enable).apply()
    fun getShowNotification(context: Context): Boolean = reader(context).getBoolean(SHOW_NOTIFICATION, false)

    fun setMorningStartTime(context: Context, startTime: Int) = editor(context).putInt(MORNING_START_TIME, startTime).apply()
    fun getMorningStartTime(context: Context): Int = reader(context).getInt(MORNING_START_TIME, 700)

    fun setMorningEndTime(context: Context, endTime: Int) = editor(context).putInt(MORNING_END_TIME, endTime).apply()
    fun getMorningEndTime(context: Context): Int = reader(context).getInt(MORNING_END_TIME, 1000)

    fun setEveningStartTime(context: Context, startTime: Int) = editor(context).putInt(EVENING_START_TIME, startTime).apply()
    fun getEveningStartTime(context: Context): Int = reader(context).getInt(EVENING_START_TIME, 1600)

    fun setEveningEndTime(context: Context, endTime: Int) = editor(context).putInt(EVENING_END_TIME, endTime).apply()
    fun getEveningEndTime(context: Context): Int = reader(context).getInt(EVENING_END_TIME, 1900)

    fun setCustomStartTime(context: Context, startTime: Int) = editor(context).putInt(CUSTOM_START_TIME, startTime).apply()
    fun getCustomStartTime(context: Context): Int = reader(context).getInt(CUSTOM_START_TIME, 1100)

    fun setCustomEndTime(context: Context, endTime: Int) = editor(context).putInt(CUSTOM_END_TIME, endTime).apply()
    fun getCustomEndTime(context: Context): Int = reader(context).getInt(CUSTOM_END_TIME, 1300)

    fun setCustomLocationName(context: Context, locationName: String) = editor(context).putString(CUSTOM_LOCATION_NAME, locationName).apply()
    fun getCustomLocationName(context: Context): String = reader(context).getString(CUSTOM_LOCATION_NAME, "") ?: ""

    fun setUseTimesToLaunchMaps(context: Context, enabled: Boolean) = editor(context).putBoolean(USE_TIMES_TO_LAUNCH_MAPS, enabled).apply()
    fun getUseTimesToLaunchMaps(context: Context): Boolean = reader(context).getBoolean(USE_TIMES_TO_LAUNCH_MAPS, false)

    fun setTurnWifiOffDevices(context: Context, turnWifiOffDevices: MutableSet<String>) = editor(context).putStringSet(TURN_WIFI_OFF_DEVICES, turnWifiOffDevices).apply()
    fun getTurnWifiOffDevices(context: Context): MutableSet<String> = reader(context).getStringSet(TURN_WIFI_OFF_DEVICES, mutableSetOf()) ?: mutableSetOf()

    fun setCloseWazeOnDisconnect(context: Context, enabled: Boolean) = editor(context).putBoolean(CLOSE_WAZE_ON_DISCONNECT, enabled).apply()
    fun getCloseWazeOnDisconnect(context: Context): Boolean = reader(context).getBoolean(CLOSE_WAZE_ON_DISCONNECT, true)

    fun setUserSetMaxVolume(context: Context, volume: Int) = editor(context).putInt(USER_SET_MAX_VOLUME_KEY, volume).apply()
    fun getUserSetMaxVolume(context: Context): Int = reader(context).getInt(USER_SET_MAX_VOLUME_KEY, VolumeControl.getDeviceMaxVolume(context))

    fun setHeadphonePreferredVolume(context: Context, volume: Int) = editor(context).putInt(HEADPHONE_PREFERRED_VOLUME_KEY, volume).apply()
    fun getHeadphonePreferredVolume(context: Context): Int = reader(context).getInt(HEADPHONE_PREFERRED_VOLUME_KEY, 7)

    fun setBrightTime(context: Context, time: Int) = editor(context).putInt(BRIGHT_TIME_KEY, time).apply()
    fun getBrightTime(context: Context): Int = reader(context).getInt(BRIGHT_TIME_KEY, 700)

    fun setDimTime(context: Context, time: Int) = editor(context).putInt(DIM_TIME_KEY, time).apply()
    fun getDimTime(context: Context): Int = reader(context).getInt(DIM_TIME_KEY, 2000)

    fun setCustomDaysToLaunchMaps(context: Context, customDays: MutableSet<String>) = editor(context).putStringSet(CUSTOM_DAYS_TO_LAUNCH_MAPS_KEY, customDays).apply()
    fun getCustomDaysToLaunchMaps(context: Context): MutableSet<String>? = reader(context).getStringSet(CUSTOM_DAYS_TO_LAUNCH_MAPS_KEY, mutableSetOf<String>())

    fun setWorkDaysToLaunchMaps(context: Context, _stringSet: MutableSet<String>) = editor(context).putStringSet(WORK_DAYS_TO_LAUNCH_MAPS_KEY, _stringSet).apply()
    fun getWorkDaysToLaunchMaps(context: Context): MutableSet<String>? = reader(context).getStringSet(WORK_DAYS_TO_LAUNCH_MAPS_KEY, launchDays)

    fun setHomeDaysToLaunchMaps(context: Context, _stringSet: MutableSet<String>) = editor(context).putStringSet(DAYS_TO_LAUNCH_MAPS_KEY, _stringSet).apply()
    fun getHomeDaysToLaunchMaps(context: Context): MutableSet<String>? = reader(context).getStringSet(DAYS_TO_LAUNCH_MAPS_KEY, launchDays)

    fun setAutoBrightness(context: Context, enabled: Boolean) = editor(context).putBoolean(AUTO_BRIGHTNESS_KEY, enabled).apply()
    fun getAutoBrightness(context: Context): Boolean = reader(context).getBoolean(AUTO_BRIGHTNESS_KEY, false)

    fun setLaunchGoogleMaps(context: Context, enabled: Boolean) = editor(context).putBoolean(LAUNCH_MAPS_KEY, enabled).apply()
    fun getLaunchGoogleMaps(context: Context): Boolean = reader(context).getBoolean(LAUNCH_MAPS_KEY, false)

    fun setKeepScreenON(context: Context, enabled: Boolean) = editor(context).putBoolean(KEEP_SCREEN_ON_KEY, enabled).apply()
    fun getKeepScreenON(context: Context): Boolean = reader(context).getBoolean(KEEP_SCREEN_ON_KEY, false)

    fun setPriorityMode(context: Context, enabled: Boolean) = editor(context).putBoolean(PRIORITY_MODE_KEY, enabled).apply()
    fun getPriorityMode(context: Context): Boolean = reader(context).getBoolean(PRIORITY_MODE_KEY, false)

    fun setMaxVolume(context: Context, enabled: Boolean) = editor(context).putBoolean(MAX_VOLUME_KEY, enabled).apply()
    fun getMaxVolume(context: Context): Boolean = reader(context).getBoolean(MAX_VOLUME_KEY, false)

    fun setLaunchMusicPlayer(context: Context, enabled: Boolean) = editor(context).putBoolean(LAUNCH_MUSIC_PLAYER_KEY, enabled).apply()
    fun getLaunchMusicPlayer(context: Context): Boolean = reader(context).getBoolean(LAUNCH_MUSIC_PLAYER_KEY, false)

    fun setPkgSelectedMusicPlayer(context: Context, packageName: String) = editor(context).putString(PKG_SELECTED_MUSIC_PLAYER_KEY, packageName).apply()
    fun getPkgSelectedMusicPlayer(context: Context): String = reader(context).getString(PKG_SELECTED_MUSIC_PLAYER_KEY, PackageHelper.GOOGLEPLAYMUSIC) ?: PackageHelper.GOOGLEPLAYMUSIC

    fun setUnlockScreen(context: Context, enabled: Boolean) = editor(context).putBoolean(UNLOCK_SCREEN_KEY, enabled).apply()
    fun getUnlockScreen(context: Context): Boolean = reader(context).getBoolean(UNLOCK_SCREEN_KEY, false)

    fun setHeadphoneDevices(context: Context, headphoneDevices: MutableSet<String>) = editor(context).putStringSet(HEADPHONE_DEVICES_KEY, headphoneDevices).apply()
    fun getHeadphoneDevices(context: Context): MutableSet<String> = reader(context).getStringSet(HEADPHONE_DEVICES_KEY, mutableSetOf()) ?: mutableSetOf()

    fun setBTDevices(context: Context, _stringSet: MutableSet<String>) = editor(context).putStringSet(BTDEVICES_KEY, _stringSet).apply()
    fun getBTDevices(context: Context): MutableSet<String> = reader(context).getStringSet(BTDEVICES_KEY, mutableSetOf()) ?: mutableSetOf()

    fun setMapsChoice(context: Context, SelectedMapsApp: String) = editor(context).putString(MAPS_CHOICE_KEY, SelectedMapsApp).apply()
    fun getMapsChoice(context: Context): String = reader(context).getString(MAPS_CHOICE_KEY, PackageHelper.MAPS) ?: PackageHelper.MAPS

    fun setAutoplayMusic(context: Context, enabled: Boolean) = editor(context).putBoolean(AUTOPLAY_MUSIC_KEY, enabled).apply()
    fun getAutoPlayMusic(context: Context): Boolean = reader(context).getBoolean(AUTOPLAY_MUSIC_KEY, true)

    fun setPowerConnected(context: Context, enabled: Boolean) = editor(context).putBoolean(POWER_CONNECTED_KEY, enabled).apply()
    fun getPowerConnected(context: Context): Boolean = reader(context).getBoolean(POWER_CONNECTED_KEY, false)

    fun setSendToBackground(context: Context, enabled: Boolean) = editor(context).putBoolean(SEND_TO_BACKGROUND_KEY, enabled).apply()
    fun getSendToBackground(context: Context): Boolean = reader(context).getBoolean(SEND_TO_BACKGROUND_KEY, false)

    fun setWaitTillOffPhone(context: Context, enabled: Boolean) = editor(context).putBoolean(WAIT_TILL_OFF_PHONE_KEY, enabled).apply()
    fun getWaitTillOffPhone(context: Context): Boolean = reader(context).getBoolean(WAIT_TILL_OFF_PHONE_KEY, true)

    //Writes to SharedPreferences, but still need to commit setting to save it
    private fun editor(context: Context): SharedPreferences.Editor = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()

    //Reads SharedPreferences value
    private fun reader(context: Context): SharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
}
