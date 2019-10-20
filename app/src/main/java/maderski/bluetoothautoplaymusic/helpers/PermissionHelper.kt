package maderski.bluetoothautoplaymusic.helpers

import android.app.Activity
import android.content.Context

interface PermissionHelper {
    fun checkToLaunchSystemOverlaySettings(activity: Activity)
    fun hasOverlayPermission(context: Context): Boolean
    fun launchSystemOverlayPermissionSettings(activity: Activity)
    fun checkToLaunchNotificationListenerSettings(activity: Activity)
    fun hasNotificationListenerAccessPermission(context: Context): Boolean
    fun launchNotificationListenerSettings(activity: Activity)
}