package maderski.bluetoothautoplaymusic.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * Created by Jason on 3/10/17.
 */

object ReceiverUtils {
    private val TAG = "ReceiverHelper"

    // Start a receiver
    fun startReceiver(context: Context, receiverClass: Class<*>) {
        Log.d(TAG, receiverClass.simpleName + " STARTED!")
        try {
            val receiver = ComponentName(context, receiverClass)
            val packageManager = context.packageManager
            packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    // Stop a receiver
    fun stopReceiver(context: Context, receiverClass: Class<*>) {
        Log.d(TAG, receiverClass.simpleName + " STOPPED!")
        try {
            val btStateReceiver = ComponentName(context, receiverClass)
            val packageManager = context.packageManager
            packageManager.setComponentEnabledSetting(btStateReceiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }
}
