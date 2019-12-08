package maderski.bluetoothautoplaymusic.utils

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Process
import androidx.annotation.RequiresApi
import androidx.annotation.StringDef
import androidx.core.app.ActivityCompat
import maderski.bluetoothautoplaymusic.services.BAPMNotificationListenerService

/**
 * Created by Jason on 9/10/16.
 */
object PermissionUtils {
    @StringDef(
            COARSE_LOCATION
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Permission

    const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private const val GET_USAGE_STATS = "android:get_usage_stats"

    fun checkPermission(activity: Activity, permission: String) {
        val packageManager = activity.packageManager
        val hasPermission = packageManager.checkPermission(permission,
                activity.packageName)
        // Check if Permission is granted
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    PackageManager.PERMISSION_GRANTED)
        }
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        val packageManager = context.packageManager
        val hasPermission = packageManager.checkPermission(permission,
                context.packageName)
        // Check if Permission is granted
        return hasPermission == PackageManager.PERMISSION_GRANTED

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkDoNotDisturbPermission(context: Context, seconds: Int): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val hasDoNotDisturbPerm = notificationManager.isNotificationPolicyAccessGranted
        if (!hasDoNotDisturbPerm) {
            val milliseconds = (seconds * 1000).toLong()
            val handler = Handler()
            val runnable = Runnable {
                val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            handler.postDelayed(runnable, milliseconds)
        }
        return hasDoNotDisturbPerm
    }

    fun checkNotificationListenerPermission(context: Context) {
        val hasPermission = BAPMNotificationListenerService.isEnabled(context)
        if (!hasPermission) {
            context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(GET_USAGE_STATS, Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun hasNotificationAccessPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = Manifest.permission.ACCESS_NOTIFICATION_POLICY
            isPermissionGranted(context, permission)
        } else {
            true
        }
    }
}
