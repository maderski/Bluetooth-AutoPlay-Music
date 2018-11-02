package maderski.bluetoothautoplaymusic.sharedprefs

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Jason on 8/6/16.
 */
object BAPMDataPreferences {
    private const val MY_PREFS_NAME = "BAPMDataPreference"

    private const val RAN_ACTIONS_ON_BT_CONNECT_KEY = "RanActionsOnBTConnect"
    private const val CURRENT_RINGER_SET = "CurrentRingerSet"
    private const val ORIGINAL_MEDIA_VOLUME = "OriginalMediaVolume"
    private const val LAUNCH_NOTIF_PRESENT = "LaunchNotifPresent"
    private const val IS_HEADPHONES_DEVICE = "IsHeadphonesDevice"
    private const val IS_TURN_OFF_WIFI_DEVICE = "IsTurnOffWifiDevice"

    fun setIsTurnOffWifiDevice(context: Context, isTurnOffWifiDevice: Boolean) = editor(context).putBoolean(IS_TURN_OFF_WIFI_DEVICE, isTurnOffWifiDevice).apply()
    fun getIsTurnOffWifiDevice(context: Context): Boolean = reader(context).getBoolean(IS_TURN_OFF_WIFI_DEVICE, false)

    fun setIsHeadphonesDevice(context: Context, isHeadphones: Boolean) = editor(context).putBoolean(IS_HEADPHONES_DEVICE, isHeadphones).apply()
    fun getIsAHeadphonesDevice(context: Context): Boolean = reader(context).getBoolean(IS_HEADPHONES_DEVICE, false)

    fun setLaunchNotifPresent(context: Context, enabled: Boolean) = editor(context).putBoolean(LAUNCH_NOTIF_PRESENT, enabled).apply()
    fun getLaunchNotifPresent(context: Context): Boolean = reader(context).getBoolean(LAUNCH_NOTIF_PRESENT, false)

    fun setOriginalMediaVolume(context: Context, volumeLevel: Int) = editor(context).putInt(ORIGINAL_MEDIA_VOLUME, volumeLevel).apply()
    fun getOriginalMediaVolume(context: Context): Int = reader(context).getInt(ORIGINAL_MEDIA_VOLUME, 7)

    fun setCurrentRingerSet(context: Context, ringerSetting: Int) = editor(context).putInt(CURRENT_RINGER_SET, ringerSetting).apply()
    fun getCurrentRingerSet(context: Context): Int = reader(context).getInt(CURRENT_RINGER_SET, 2)

    fun setRanActionsOnBtConnect(context: Context, enabled: Boolean) = editor(context).putBoolean(RAN_ACTIONS_ON_BT_CONNECT_KEY, enabled).apply()
    fun getRanActionsOnBtConnect(context: Context): Boolean = reader(context).getBoolean(RAN_ACTIONS_ON_BT_CONNECT_KEY, false)

    //Writes to SharedPreferences, but still need to commit setting to save it
    private fun editor(context: Context): SharedPreferences.Editor = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()

    //Reads SharedPreferences value
    private fun reader(context: Context): SharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
}
