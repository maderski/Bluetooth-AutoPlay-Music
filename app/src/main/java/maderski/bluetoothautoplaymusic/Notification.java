package maderski.bluetoothautoplaymusic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.PriorityQueue;

/**
 * Created by Jason on 12/8/15.
 */
public class Notification {
    //private static final String title = "Bluetooth Connect and Do Stuff";
    private static final String message = "Device " + VariableStore.btDevice + " connected";
    private static final String nTAG = Notification.class.getName();
    private static final int nID = 608;

    public static void BAPMMessage(Context context){
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent mapIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, LaunchMapActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                //.setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(android.R.drawable.ic_dialog_map, "Map", mapIntent)
                .addAction(android.R.drawable.ic_menu_edit, "Options", contentIntent);
        nManager.notify(nTAG, nID, builder.build());
    }

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

