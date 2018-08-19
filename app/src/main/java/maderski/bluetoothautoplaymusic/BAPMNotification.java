package maderski.bluetoothautoplaymusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;


/**
 * Created by Jason on 12/8/15.
 */
public class BAPMNotification {

    public static final String TAG = "BAPMNotification";

    private static final int NOTIFICATION_ID = 608;
    private static final String CHANNEL_ID = "BTAPMChannelIDNotification";
    private static final String CHANNEL_NAME = "Bluetooth Autoplay Music Notification";

    //Create notification message for BAPM
    public void BAPMMessage(Context context, String mapChoicePkg){
        int color = ContextCompat.getColor(context, R.color.colorAccent);
        String mapAppName = "GOOGLE MAPS";
        if(BAPMPreferences.getMapsChoice(context).equalsIgnoreCase(PackageTools.PackageName.WAZE)){
            mapAppName = "WAZE";
        }

        String title = "Click to launch " + mapAppName;
        String message = "Bluetooth device connected";

        NotificationManager nManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setAutoCancel(false)
                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Intent appLaunchIntent = context.getPackageManager().getLaunchIntentForPackage(mapChoicePkg);
        if(appLaunchIntent != null) {
            appLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mapIntent = PendingIntent.getActivity(context, 0, appLaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(mapIntent);
            builder.setContentTitle(title);
        } else {
            builder.setContentTitle("Bluetooth Autoplay Music");
        }

        if(nManager != null) {
            createNotificationChannel(nManager);
            nManager.notify(TAG, NOTIFICATION_ID, builder.build());
        }
    }

    public void launchBAPM(Context context){
        int color = ContextCompat.getColor(context, R.color.colorAccent);

        String title = "Launch Bluetooth Autoplay Music";
        String message = "Bluetooth device connected";

        BAPMDataPreferences.setLaunchNotifPresent(context, true);

        Intent launchBAPMIntent = new Intent();
        launchBAPMIntent.setAction("maderski.bluetoothautoplaymusic.offtelephonelaunch");
        PendingIntent appIntent = PendingIntent.getBroadcast(context, 0, launchBAPMIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setAutoCancel(false)
                .setContentIntent(appIntent)
                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(android.app.Notification.DEFAULT_VIBRATE);
        if(nManager != null) {
            createNotificationChannel(nManager);
            nManager.notify(TAG, NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotificationChannel(NotificationManager nManager) {
        if(Build.VERSION.SDK_INT > 25) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nManager.createNotificationChannel(notificationChannel);
        }
    }

    //Remove notification that was created by BAPM
    public void removeBAPMMessage(Context context){
        NotificationManager nManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if(nManager != null) {
            nManager.cancel(TAG, NOTIFICATION_ID);
            BAPMDataPreferences.setLaunchNotifPresent(context, false);
        }
    }
}

