package maderski.bluetoothautoplaymusic.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.WAZE
import maderski.bluetoothautoplaymusic.wrappers.AndroidSystemServicesWrapper


/**
 * Created by Jason on 12/8/15.
 */
class BAPMNotification(
        private val context: Context,
        private val preferencesHelper: PreferencesHelper,
        systemServicesWrapper: AndroidSystemServicesWrapper
) {
    private val nManager = systemServicesWrapper.notificationManager

    //Create notification message for BAPM
    fun bapmMessage(mapChoicePkg: String) {
        val color = ContextCompat.getColor(context, R.color.colorAccent)
        val mapAppName = if (preferencesHelper.mapAppChosen.equals(WAZE.packageName, ignoreCase = true)) {
            "WAZE"
        } else {
            "GOOGLE MAPS"
        }

        val title = context.getString(R.string.click_to_launch) + mapAppName
        val message = context.getString(R.string.bluetooth_device_connected)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setAutoCancel(false)
                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_MAX)

        val appLaunchIntent = context.packageManager.getLaunchIntentForPackage(mapChoicePkg)
        if (appLaunchIntent != null) {
            appLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val mapIntent = PendingIntent.getActivity(context, 0, appLaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(mapIntent)
            builder.setContentTitle(title)
        } else {
            builder.setContentTitle("Bluetooth Autoplay Music")
        }

        postNotification(builder)
    }

    fun launchBAPM() {
        val color = ContextCompat.getColor(context, R.color.colorAccent)

        val title = context.getString(R.string.launch_bluetooth_autoplay)
        val message = context.getString(R.string.bluetooth_device_connected)

        preferencesHelper.isLaunchBTAPMNotifShowing = true

        val launchBAPMIntent = Intent()
        launchBAPMIntent.action = "maderski.bluetoothautoplaymusic.offtelephonelaunch"
        val appIntent = PendingIntent.getBroadcast(context, 0, launchBAPMIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = buildNotification(title, message, appIntent, color)
        postNotification(builder)
    }

    private fun buildNotification(title: String, message: String, appIntent: PendingIntent, @ColorInt color: Int): NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notif_icon)
            .setAutoCancel(false)
            .setContentIntent(appIntent)
            .setColor(color)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(android.app.Notification.DEFAULT_VIBRATE)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > 25) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            nManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun postNotification(builder: NotificationCompat.Builder) {
        nManager.let { notificationManager ->
            createNotificationChannel()
            notificationManager.notify(TAG, NOTIFICATION_ID, builder.build())
        }
    }

    //Remove notification that was created by BAPM
    fun removeBAPMMessage() {
        nManager.let { notificationManager ->
            notificationManager.cancel(TAG, NOTIFICATION_ID)

            preferencesHelper.isLaunchBTAPMNotifShowing = false
        }
    }

    companion object {
        const val TAG = "BAPMNotification"

        private const val NOTIFICATION_ID = 608
        private const val CHANNEL_ID = "BTAPMChannelIDNotification"
        private const val CHANNEL_NAME = "Bluetooth Autoplay Music Notification"
    }
}

