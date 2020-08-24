package maderski.bluetoothautoplaymusic.services

import android.content.Context
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat

class BAPMNotificationListenerService : NotificationListenerService() {
    companion object {
        // Helper method to check if our notification listener is enabled. In order to get active media
        // sessions, we need an enabled notification listener component.
        @JvmStatic
        fun isEnabled(context: Context): Boolean {
            return NotificationManagerCompat
                    .getEnabledListenerPackages(context)
                    .contains(context.packageName)
        }
    }
}