package maderski.bluetoothautoplaymusic.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import android.widget.Toast
import maderski.bluetoothautoplaymusic.R

object SettingsUtils {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @JvmStatic
    fun showBatteryOptimizationSettings(context: Context) {
        val appName = context.resources.getString(R.string.app_name)
        Toast.makeText(context, "Battery optimization -> All apps -> $appName -> Don't optimize", Toast.LENGTH_LONG).show()

        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        startActivity(context, intent, null)
    }
}