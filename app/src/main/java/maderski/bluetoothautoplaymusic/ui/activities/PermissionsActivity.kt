package maderski.bluetoothautoplaymusic.ui.activities

import android.content.Intent
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

        // If all permissions are granted just launch main activity
        if (permissionManager.isAllRequiredPermissionsGranted()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            setRequiredPermissionsUIState()

            bn_perm_continue.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun setRequiredPermissionsUIState() {
        // Usage Stats
        val hasUsageStatsPermission = permissionManager.hasUsageStatsPermission()
        setStatusIcon(iv_usage_stats_perm_status, hasUsageStatsPermission)
        sw_usage_stats_perm_toggle.isEnabled = hasUsageStatsPermission
        if (!hasUsageStatsPermission) {
            sw_usage_stats_perm_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
                permissionManager.checkUsageStatPermission(this)
                sw_usage_stats_perm_toggle.setOnCheckedChangeListener(null)
            }
        }

        // Notification Access
        val hasNotificationAccessPermission = permissionManager.hasNotificationAccessPermission()
        setStatusIcon(iv_notification_access_perm_status, hasNotificationAccessPermission)
        sw_notification_access_perm_toggle.isEnabled = hasNotificationAccessPermission
        if (!hasNotificationAccessPermission) {
            sw_notification_access_perm_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
                permissionManager.checkAccessNotificationPolicyPermission(this)
                sw_notification_access_perm_toggle.setOnCheckedChangeListener(null)
            }
        }

        // Overlay
        val hasOverlayPermission = permissionManager.hasOverlayPermission()
        setStatusIcon(iv_overlay_perm_status, hasOverlayPermission)
        sw_overlay_perm_toggle.isEnabled = hasOverlayPermission
        if (!hasOverlayPermission) {
            sw_overlay_perm_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
                permissionManager.checkToLaunchSystemOverlaySettings(this)
                sw_overlay_perm_toggle.setOnCheckedChangeListener(null)
            }
        }

        // Notification Listener
        val hasNotificationListenerAccessPermission = permissionManager.hasNotificationListenerAccessPermission()
        setStatusIcon(iv_notification_listener_perm_status, hasNotificationListenerAccessPermission)
        sw_notification_listener_perm_toggle.isEnabled = hasNotificationListenerAccessPermission
        if (!hasNotificationListenerAccessPermission) {
            sw_notification_listener_perm_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
                permissionManager.checkToLaunchNotificationListenerSettings(this)
                sw_notification_access_perm_toggle.setOnCheckedChangeListener(null)
            }
        }
    }

    private fun setStatusIcon(imageView: ImageView, isEnabled: Boolean) {
        val statusIconResId = if (isEnabled) R.drawable.ic_permission_granted else R.drawable.ic_no_permission
        imageView.setImageDrawable(getDrawable(statusIconResId))
    }
}