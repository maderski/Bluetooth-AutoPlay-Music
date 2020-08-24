package maderski.bluetoothautoplaymusic.services.manager

import android.app.*
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class ServiceManager(
        private val context: Context,
        private val systemServicesWrapper: SystemServicesWrapper
) {
    private val tagToServiceConnectionMap = mutableMapOf<String, ServiceConnection>()

    fun startService(serviceClass: Class<*>, tag: String, serviceConnection: ServiceConnection? = null) {
        val intent = Intent(context, serviceClass)
        intent.addCategory(tag)
        // Start Foreground Service
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            ContextCompat.startForegroundService(context, intent)
        }
        // If a Service Connection Callback is present add it
        serviceConnection?.let {
            tagToServiceConnectionMap[tag] = it
            context.bindService(intent, it, BIND_AUTO_CREATE)
        }
    }

    fun stopService(serviceClass: Class<*>, tag: String) {
        try {
            val intent = Intent(context, serviceClass)
            intent.addCategory(tag)
            context.stopService(intent)

            val serviceConnection = tagToServiceConnectionMap[tag]
            serviceConnection?.let {
                context.unbindService(it)
                tagToServiceConnectionMap.remove(tag)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = systemServicesWrapper.activityManager
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
        val notification = getNotification(
                systemServicesWrapper.notificationManager,
                title,
                message,
                channelId,
                channelName,
                icon,
                isOngoing
        )
        service.startForeground(id, notification)
    }

    fun updateServiceNotification(title: String) {
        val notificationManager = systemServicesWrapper.notificationManager
        val notification = getNotification(
                notificationManager,
                title,
                context.getString(R.string.app_name),
                CHANNEL_ID_FOREGROUND_SERVICE,
                CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon,
                false
        )
        notificationManager.notify(FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
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
            priority = NotificationCompat.PRIORITY_MIN
            setNotificationSilent()
        }

        return builder.build().also {
            if (isOngoing) {
                it.flags = NotificationCompat.FLAG_FOREGROUND_SERVICE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel(channelId: String, channelName: String): NotificationChannel =
            NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_MIN).apply {
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                setSound(null, null)
                enableVibration(false)
                enableLights(false)
            }

    companion object {
        const val FOREGROUND_SERVICE_NOTIFICATION_ID = 3453
        const val CHANNEL_ID_FOREGROUND_SERVICE = "BTAPMChannelID"
        const val CHANNEL_NAME_FOREGROUND_SERVICE = "Bluetooth Autoplay Music"
    }
}