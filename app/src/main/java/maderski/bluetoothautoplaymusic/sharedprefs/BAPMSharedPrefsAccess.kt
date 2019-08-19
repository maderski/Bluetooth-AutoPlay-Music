package maderski.bluetoothautoplaymusic.sharedprefs

import android.content.Context
import android.content.SharedPreferences


class BAPMSharedPrefsAccess(private val context: Context, private val prefsFilename: String) : SharedPrefsAccess {
    override fun putBoolean(key: String, value: Boolean) = editor().putBoolean(key, value).apply()
    override fun getBoolean(key: String, defValue: Boolean): Boolean = reader().getBoolean(key, defValue)

    override fun putInt(key: String, value: Int) = editor().putInt(key, value).apply()
    override fun getInt(key: String, defValue: Int): Int = reader().getInt(key, defValue)

    override fun putString(key: String, value: String) = editor().putString(key, value).apply()
    override fun getString(key: String, defValue: String): String = reader().getString(key, defValue) ?: defValue

    override fun putStringSet(key: String, values: MutableSet<String>?) = editor().putStringSet(key, values).apply()
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = reader().getStringSet(key, defValues)

    override fun editor(): SharedPreferences.Editor = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE).edit()
    override fun reader(): SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
}