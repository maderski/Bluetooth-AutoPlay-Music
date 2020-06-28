package maderski.bluetoothautoplaymusic.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import org.koin.android.ext.android.inject

class PermissionsActivity : AppCompatActivity() {
    private val permissionManager: PermissionManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        val isLocationPermissionGranted = permissionManager.isLocationPermissionGranted()
        val hasUsageStatsPermission = permissionManager.hasUsageStatsPermission()
        val hasNotificationAccessPermission = permissionManager.hasNotificationAccessPermission()
        val hasOverlayPermission = permissionManager.hasOverlayPermission()
        val hasNotificationListenerAccessPermission = permissionManager.hasNotificationListenerAccessPermission()

    }



//    fun checkAllRequiredPermissions() {
//        checkLocationPermission(this)
//        checkToLaunchSystemOverlaySettings(activity)
//        checkToLaunchNotificationListenerSettings(activity)
//        checkNotificationListenerPermission(activity)
//    }
}