package maderski.bluetoothautoplaymusic.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.ui.activities.MainActivity
import maderski.bluetoothautoplaymusic.wrappers.PackageManagerWrapper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper


/**
 * Created by Jason on 12/8/15.
 */
class BAPMNotification(
        private val context: Context,
        systemServicesWrapper: SystemServicesWrapper,
        private val packageManagerWrapper: PackageManagerWrapper
) {
    private val notificationManager = systemServicesWrapper.notificationManager

    fun launchBAPMNotification(message: String) {
        val color = ContextCompat.getColor(context, R.color.colorAccent)

        val title = context.getString(R.string.app_name)

        val builder = buildNotification(title, message, color)
        val launchBAPMIntent = packageManagerWrapper.getLaunchIntentForPackage(context.packageName)?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(launchBAPMIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        builder.apply {
            setContentIntent(pendingIntent)
        }
        Log.d(TAG, "BAPMNotification shown with message: $message")
        postNotification(builder)
    }

    private fun buildNotification(title: String, message: String, @ColorInt color: Int): NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notif_icon)
            .setAutoCancel(false)
            .setColor(color)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(Notification.DEFAULT_VIBRATE)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > 25) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun postNotification(builder: NotificationCompat.Builder) {
        createNotificationChannel()
        notificationManager.notify(TAG, ServiceManager.FOREGROUND_SERVICE_NOTIFICATION_ID, builder.build())
    }

    companion object {
        const val TAG = "BAPMNotification"

        private const val CHANNEL_ID = "BTAPMChannelIDNotification"
        private const val CHANNEL_NAME = "Bluetooth Autoplay Music Notification"
    }
}

