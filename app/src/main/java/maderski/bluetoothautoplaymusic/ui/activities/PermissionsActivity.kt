package maderski.bluetoothautoplaymusic.ui.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_permissions.*
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import org.koin.android.ext.android.inject

class PermissionsActivity : AppCompatActivity() {
    private val permissionManager: PermissionManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        // Course Location
        val isLocationPermissionGranted = permissionManager.isLocationPermissionGranted()
        setStatusIcon(iv_location_perm_status, isLocationPermissionGranted)
        sw_location_perm_toggle.isEnabled = isLocationPermissionGranted

        // Usage Stats
        val hasUsageStatsPermission = permissionManager.hasUsageStatsPermission()
        setStatusIcon(iv_usage_stats_perm_status, hasUsageStatsPermission)
        sw_usage_stats_perm_toggle.isEnabled = hasUsageStatsPermission

        // Notification Access
        val hasNotificationAccessPermission = permissionManager.hasNotificationAccessPermission()
        setStatusIcon(iv_notification_access_perm_status, hasNotificationAccessPermission)
        sw_notification_access_perm_toggle.isEnabled = hasNotificationAccessPermission

        // Overlay
        val hasOverlayPermission = permissionManager.hasOverlayPermission()
        setStatusIcon(iv_overlay_perm_status, hasOverlayPermission)
        sw_overlay_perm_toggle.isEnabled = hasOverlayPermission

        // Notification Listener
        val hasNotificationListenerAccessPermission = permissionManager.hasNotificationListenerAccessPermission()
        setStatusIcon(iv_notification_listener_perm_status, hasNotificationListenerAccessPermission)
        sw_notification_listener_perm_toggle.isEnabled = hasNotificationListenerAccessPermission

        bn_perm_continue.setOnClickListener {

        }

        bn_perm_continue.isEnabled = isLocationPermissionGranted
                && hasNotificationAccessPermission
                && hasNotificationListenerAccessPermission
                && hasOverlayPermission
                && hasUsageStatsPermission
    }

    private fun setStatusIcon(imageView: ImageView, isEnabled: Boolean) {
        val statusIconResId = if (isEnabled) R.drawable.ic_permission_granted else R.drawable.ic_no_permission
        imageView.setImageDrawable(getDrawable(statusIconResId))
    }



//    fun checkAllRequiredPermissions() {
//        checkLocationPermission(this)
//        checkToLaunchSystemOverlaySettings(activity)
//        checkToLaunchNotificationListenerSettings(activity)
//        checkNotificationListenerPermission(activity)
//    }
}