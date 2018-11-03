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
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi

/**
 * Created by Jason on 6/6/17.
 */

object ServiceUtils {
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
        val intent = Intent(context, serviceClass)
        intent.addCategory(tag)
        context.stopService(intent)
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            val services = activityManager.getRunningServices(Integer.MAX_VALUE)

            for (runningServiceInfo in services) {
                if (runningServiceInfo.service.className == serviceClass.name) {
                    return true
                }
            }
        }
        return false
    }

    fun createServiceNotification(id: Int,
                                  title: String,
                                  message: String,
                                  service: Service,
                                  channelId: String,
                                  channelName: String,
                                  @DrawableRes icon: Int) {
        val builder: Notification.Builder

        builder = if (Build.VERSION.SDK_INT < 26) {
            android.app.Notification.Builder(service)
        } else {
            val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager != null) {
                val channel = getNotificationChannel(channelId, channelName)
                notificationManager.createNotificationChannel(channel)
            }
            Notification.Builder(service, channelId)
        }

        val notification = builder
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setOngoing(true)
                .build()

        service.startForeground(id, notification)
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

    fun scheduleJob(context: Context, jobServiceClass: Class<*>) {
        val jobServiceComponent = ComponentName(context, jobServiceClass)
        val builder = JobInfo.Builder(0, jobServiceComponent)
        builder.setMinimumLatency(1000)
        builder.setOverrideDeadline(10000)

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        jobScheduler.schedule(builder.build())
    }
}
