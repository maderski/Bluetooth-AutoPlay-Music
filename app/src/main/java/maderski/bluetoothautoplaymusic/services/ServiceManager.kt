package maderski.bluetoothautoplaymusic.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class ServiceManager (private val context: Context) {

    fun startService(serviceClass: Class<*>, tag: String) {
        val intent = Intent(context, serviceClass)
        intent.addCategory(tag)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
    }

    fun stopService(serviceClass: Class<*>, tag: String) {
        try {
            val intent = Intent(context, serviceClass)
            intent.addCategory(tag)
            context.stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
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

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotification(notificationManager, title, message, channelId, channelName, icon, isOngoing)
        service.startForeground(id, notification)
    }

    private fun getNotification(notificationManager: NotificationManager,
                                title: String,
                                message: String,
                                channelId: String,
                                channelName: String,
                                @DrawableRes icon: Int,
                                isOngoing: Boolean): Notification {

        val builder = if (Build.VERSION.SDK_INT < 26) {
            NotificationCompat.Builder(context, channelId)
        } else {
            val channel = getNotificationChannel(channelId, channelName)
            notificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(context, channelId)
        }

        with(builder) {
            setSmallIcon(icon)
            setContentTitle(title)
            setContentText(message)
            setOnlyAlertOnce(true)

            if (Build.VERSION.SDK_INT < 26 && !isOngoing)
                priority = NotificationCompat.PRIORITY_MIN
        }

        val notification = builder.build()

        notification.flags = if (isOngoing) {
            NotificationCompat.FLAG_FOREGROUND_SERVICE
        } else {
            NotificationCompat.PRIORITY_DEFAULT
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

    companion object {
        const val CHANNEL_ID_FOREGROUND_SERVICE = "BTAPMChannelID"
        const val CHANNEL_NAME_FOREGROUND_SERVICE = "Bluetooth Autoplay Music"
    }
}