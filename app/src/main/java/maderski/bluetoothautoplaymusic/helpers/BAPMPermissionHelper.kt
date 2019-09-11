package maderski.bluetoothautoplaymusic.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

class BAPMPermissionHelper : PermissionHelper {
    override fun checkToLaunchSystemOverlaySettings(activity: Activity) {
        val hasOverlayPermission = hasOverlayPermission(activity)
        if (!hasOverlayPermission) {
            launchSystemOverlayPermissionSettings(activity)
        }
    }

    override fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            // Using less than API 23, so permission is not required
            true
        }
    }

    override fun launchSystemOverlayPermissionSettings(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val launchSettingsIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity.packageName}"))
            activity.startActivityForResult(launchSettingsIntent, DRAW_OVER_OTHER_APPS_PERMISSION)
        }
    }

    companion object {
        const val DRAW_OVER_OTHER_APPS_PERMISSION = 1000
    }
}