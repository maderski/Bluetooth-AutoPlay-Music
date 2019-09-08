package maderski.bluetoothautoplaymusic.utils

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * Created by Jason on 6/6/17.
 */

object serviceManager {
    const val CHANNEL_ID_FOREGROUND_SERVICE = "BTAPMChannelID"
    const val CHANNEL_NAME_FOREGROUND_SERVICE = "Bluetooth Autoplay Music"

    fun startService(context: Context, serviceClass: Class<*>, tag: String) {
        val intent = Intent(context, serviceClass)
        intent.addCategory(tag)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
    }

    fun stopService(context: Context, serviceClass: Class<*>, tag: String) {
        try {
            val intent = Intent(context, serviceClass)
            intent.addCategory(tag)
            context.stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)

        return services.any { runningServiceInfo ->
            runningServiceInfo.service.className == serviceClass.name
        }
    }

    fun createServiceNotification(id: Int,
                                  title: String,
                                  message: String,
                                  service: Service,
                                  channelId: String,
                                  channelName: String,
                                  @DrawableRes icon: Int,
                                  isOngoing: Boolean) {

        val notification = getNotification(title, message, service, channelId, channelName, icon, isOngoing)
        service.startForeground(id, notification)
    }

    private fun getNotification(title: String,
                                message: String,
                                context: Context,
                                channelId: String,
                                channelName: String,
                                @DrawableRes icon: Int,
                                isOngoing: Boolean): Notification {
        val builder: NotificationCompat.Builder

        builder = if (Build.VERSION.SDK_INT < 26) {
            NotificationCompat.Builder(context, channelId)
        } else {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = getNotificationChannel(channelId, channelName)
            notificationManager.createNotificationChannel(channel)

            NotificationCompat.Builder(context, channelId)
        }

        val notification = builder
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setOnlyAlertOnce(true)
                .build()

        if (isOngoing) {
            notification.flags = NotificationCompat.FLAG_FOREGROUND_SERVICE
        }

        return notification
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel(channelId: String, channelName: String): NotificationChannel {
        val notificationChannel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_MIN)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationChannel.setSound(null, null)
        notificationChannel.enableVibration(false)
        return notificationChannel
    }
}
