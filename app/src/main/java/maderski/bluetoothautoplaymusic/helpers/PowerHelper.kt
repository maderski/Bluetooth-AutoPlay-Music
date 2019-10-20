package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.util.Log

/**
 * Created by Jason on 2/21/16.
 */
class PowerHelper(private val context: Context) {

    // Returns true or false depending if battery is plugged in
    fun isPluggedIn(): Boolean {
        val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        var chargePlug = 0
        if (batteryStatus != null) {
            chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        }

        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val wirelessCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS

        return usbCharge || acCharge || wirelessCharge
    }

    // Return false if in settings "Not optimized" and true if "Optimizing battery use"
    fun isBatteryOptimized(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = context.packageName

            powerManager.isIgnoringBatteryOptimizations(packageName).not()
        } else {
            false
        }
    }
}
