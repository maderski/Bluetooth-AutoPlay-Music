package maderski.bluetoothautoplaymusic.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import maderski.bluetoothautoplaymusic.bluetooth.models.BAPMDevice
import maderski.bluetoothautoplaymusic.helpers.SerializationHelper


class BAPMSharedPrefsAccess(
        private val context: Context,
        private val prefsFilename: String,
        private val serializationHelper: SerializationHelper
) : SharedPrefsAccess {
    override fun putBoolean(key: String, value: Boolean) = editor().putBoolean(key, value).apply()
    override fun getBoolean(key: String, defValue: Boolean): Boolean = reader().getBoolean(key, defValue)

    override fun putInt(key: String, value: Int) = editor().putInt(key, value).apply()
    override fun getInt(key: String, defValue: Int): Int = reader().getInt(key, defValue)

    override fun putString(key: String, value: String) = editor().putString(key, value).apply()
    override fun getString(key: String, defValue: String): String = reader().getString(key, defValue) ?: defValue

    override fun putStringSet(key: String, values: Set<String>?) = editor().putStringSet(key, values).apply()
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String> =
            reader().getStringSet(key, defValues) ?: setOf()

    override fun putBAPMDeviceSet(key: String, values: Set<BAPMDevice>?) {
        values?.let {
            val serializedValue = serializationHelper.serializeBTDeviceSet(it)
            editor().putString(key, serializedValue).apply()
        }
    }

    override fun getBAPMDeviceSet(key: String, defValues: Set<BAPMDevice>?): Set<BAPMDevice> {
        val defSerializedValues = if (defValues != null) serializationHelper.serializeBTDeviceSet(defValues) else ""
        val serializedValues = reader().getString(key, defSerializedValues) ?: ""
        return if (serializedValues.isNotEmpty()) serializationHelper.deserializeBTDeviceSet(serializedValues) else emptySet()
    }

    override fun editor(): SharedPreferences.Editor = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE).edit()
    override fun reader(): SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
}