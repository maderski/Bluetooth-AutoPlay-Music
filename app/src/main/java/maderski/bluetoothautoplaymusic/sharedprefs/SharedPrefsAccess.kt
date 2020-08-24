package maderski.bluetoothautoplaymusic.sharedprefs

import android.content.SharedPreferences
import maderski.bluetoothautoplaymusic.bluetooth.models.BAPMDevice

interface SharedPrefsAccess {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defValue: Boolean): Boolean

    fun putInt(key: String, value: Int)
    fun getInt(key: String, defValue: Int): Int

    fun putString(key: String, value: String)
    fun getString(key: String, defValue: String): String

    fun putStringSet(key: String, values: Set<String>?)
    fun getStringSet(key: String, defValues: Set<String>?): Set<String>

    fun putBAPMDeviceSet(key: String, values: Set<BAPMDevice>?)
    fun getBAPMDeviceSet(key: String, defValues: Set<BAPMDevice>?): Set<BAPMDevice>

    fun editor(): SharedPreferences.Editor
    fun reader(): SharedPreferences
}