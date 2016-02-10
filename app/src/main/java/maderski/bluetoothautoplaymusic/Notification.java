package maderski.bluetoothautoplaymusic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * Created by Jason on 12/8/15.
 */
public class Notification {

    private static final String nTAG = Notification.class.getName();
    private static final int nID = 608;

    //Create notification message for BAPM
    public static void BAPMMessage(Context context, String btDevice){
        int color = ContextCompat.getColor(context, R.color.colorAccent);
        String message = "Device " + btDevice + " connected";

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent mapIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, LaunchMapActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setAutoCancel(false)
                //.setContentIntent(expandIntent)
                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(android.R.drawable.ic_dialog_map, "Map", mapIntent)
                .addAction(android.R.drawable.ic_menu_edit, "Options", contentIntent);
        nManager.notify(nTAG, nID, builder.build());
    }

    //Remove notification that was created by BAPM
    public static void removeBAPMMessage(Context context){
        NotificationManager nManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            nManager.cancel(nTAG, nID);
        }catch(Exception e){
            Log.e(nTAG, e.getMessage());
        }
    }
}

