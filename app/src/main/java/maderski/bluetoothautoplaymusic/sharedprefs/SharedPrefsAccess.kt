package maderski.bluetoothautoplaymusic.sharedprefs

import android.content.SharedPreferences

interface SharedPrefsAccess {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defValue: Boolean): Boolean

    fun putInt(key: String, value: Int)
    fun getInt(key: String, defValue: Int): Int

    fun putString(key: String, value: String)
    fun getString(key: String, defValue: String): String

    fun putStringSet(key: String, values: MutableSet<String>?)
    fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>?

    fun editor(): SharedPreferences.Editor
    fun reader(): SharedPreferences
}