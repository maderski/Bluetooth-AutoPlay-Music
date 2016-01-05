package maderski.bluetoothautoplaymusic;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Jason on 12/8/15.
 */
public class Notification {
    private static final String title = "Play Music On Connect";
    private static final String message = "Wakelock: ENABLED and Phone SILENCED";
    private static final String nTAG = Notification.class.getName();
    private static final int nID = 608;

    public static void BAPMMessage(Context context){
        NotificationManager nManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false);
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

