package maderski.bluetoothautoplaymusic.power

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 2/21/16.
 */
class PowerInfo(
        private val context: Context,
        private val systemServicesWrapper: SystemServicesWrapper
) {

    // Returns true or false depending if battery is plugged in
    fun isPluggedIn(): Boolean {
        val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return batteryStatus?.let {
            val chargePlug = it.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
            val wirelessCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS
            usbCharge || acCharge || wirelessCharge
        } ?: false
    }

    // Return false if in settings "Not optimized" and true if "Optimizing battery use"
    fun isBatteryOptimized(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = systemServicesWrapper.powerManager
            val packageName = context.packageName

            powerManager.isIgnoringBatteryOptimizations(packageName).not()
        } else {
            false
        }
    }
}
