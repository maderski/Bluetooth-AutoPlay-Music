package maderski.bluetoothautoplaymusic.permission

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.app.ActivityCompat
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class PermissionManager(
        private val context: Context,
        private val systemServicesWrapper: SystemServicesWrapper
) {
    //region Permission status
    fun isAllRequiredPermissionsGranted() = hasNotificationAccessPermission()
            && hasOverlayPermission()
            && hasNotificationListenerAccessPermission()

    fun isLocationPermissionGranted() = isPermissionGranted(Permission.COARSE_LOCATION)

    fun hasNotificationAccessPermission(): Boolean = isPermissionGranted(Permission.ACCESS_NOTIFICATION_POLICY)

    fun hasOverlayPermission(): Boolean = Settings.canDrawOverlays(context)

    fun hasNotificationListenerAccessPermission(): Boolean {
        return try {
            val enabledNotificationListeners: String? = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            enabledNotificationListeners?.contains(context.packageName) ?: false
        } catch (e: Exception) {
            false
        }
    }
    //endRegion

    //region Check Permission and if not granted launch
    fun checkLocationPermission(activity: Activity) {
        checkPermission(activity, Permission.COARSE_LOCATION)
    }

    fun checkAccessNotificationPolicyPermission(activity: Activity) {
        checkPermission(activity, Permission.ACCESS_NOTIFICATION_POLICY)
    }

    fun checkDoNotDisturbPermission(): Boolean {
        val notificationManager = systemServicesWrapper.notificationManager
        val hasDoNotDisturbPerm = notificationManager.isNotificationPolicyAccessGranted
        if (!hasDoNotDisturbPerm) {
            // Launch settings screen for Notification Policy access that is required for Do Not Disturbed
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
        return hasDoNotDisturbPerm
    }

    fun checkToLaunchSystemOverlaySettings(activity: Activity) {
        if (!hasOverlayPermission()) {
            launchSystemOverlayPermissionSettings(activity)
        }
    }

    fun checkToLaunchNotificationListenerSettings(activity: Activity) {
        if (!hasNotificationListenerAccessPermission()) {
            launchNotificationListenerSettings(activity)
        }
    }
    //endRegion

    private fun launchSystemOverlayPermissionSettings(activity: Activity) {
        val launchSettingsIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${activity.packageName}")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivityForResult(launchSettingsIntent, DRAW_OVER_OTHER_APPS_PERMISSION)
    }

    private fun launchNotificationListenerSettings(activity: Activity) =
        activity.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

    private fun checkPermission(activity: Activity, permission: Permission) {
        val packageManager = activity.packageManager
        val hasPermission = packageManager.checkPermission(permission.value,
                activity.packageName)
        // Check if Permission is granted
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission.value),
                    PackageManager.PERMISSION_GRANTED)
        }
    }

    private fun isPermissionGranted(permission: Permission): Boolean {
        val packageManager = context.packageManager
        val hasPermission = packageManager.checkPermission(
                permission.value,
                context.packageName
        )
        // Check if Permission is granted
        return hasPermission == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val DRAW_OVER_OTHER_APPS_PERMISSION = 1000
        const val NOTIFICATION_LISTENER_ACCESS_PERMISSION = 2000
    }
}