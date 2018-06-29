package maderski.bluetoothautoplaymusic.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;

import maderski.bluetoothautoplaymusic.BAPMNotification;
import maderski.bluetoothautoplaymusic.R;

/**
 * Created by Jason on 6/6/17.
 */

public class ServiceUtils {
    public static void startService(Context context, Class<?> serviceClass, String tag) {
        Intent intent = new Intent(context, serviceClass);
        intent.addCategory(tag);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
    }

    public static void stopService(Context context, Class<?> serviceClass, String tag) {
        Intent intent = new Intent(context, serviceClass);
        intent.addCategory(tag);
        context.stopService(intent);
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null) {
            List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

            for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
                if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void createServiceNotification(int id, String title, String message, Service service, String channelId, String channelName) {
        Notification.Builder builder;

        if(Build.VERSION.SDK_INT < 26) {
            builder = new android.app.Notification.Builder(service);
        } else {
            NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                NotificationChannel channel = ServiceUtils.getNotificationChannel(channelId, channelName);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new Notification.Builder(service, channelId);
        }

        Notification notification = builder
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setContentTitle(title)
                .setContentText(message)
                .build();

        service.startForeground(id, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static NotificationChannel getNotificationChannel(String channelId, String channelName) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_UNSPECIFIED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationChannel.setSound(null, null);
        notificationChannel.enableVibration(false);
        return notificationChannel;
    }

    public static void scheduleJob(Context context, Class<?> jobServiceClass) {
        ComponentName jobServiceComponent = new ComponentName(context, jobServiceClass);
        JobInfo.Builder builder = new JobInfo.Builder(0, jobServiceComponent);
        builder.setMinimumLatency(1000);
        builder.setOverrideDeadline(10000);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
    }
}
