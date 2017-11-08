package maderski.bluetoothautoplaymusic.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import maderski.bluetoothautoplaymusic.BluetoothActions.BTDisconnectActions;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.UI.activities.MainActivity;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 7/8/17.
 */

public class BTDisconnectService extends Service {
    public static final String TAG = "BTDisconnectService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BTDisconnectActions btDisconnectActions = new BTDisconnectActions(this);
        btDisconnectActions.actionsOnBTDisconnect();

        if(BuildConfig.DEBUG){
            Log.d(TAG, "BT DISCONNECT SERVICE STARTED");
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String title = "Putting stuff back :)";
        String message = "Bluetooth Autoplay Music";
        ServiceUtils.createServiceNotification(3454, title, message, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(BuildConfig.DEBUG){
            Log.d(TAG, "BT DISCONNECT SERVICE STOPPED");
        }

        stopForeground(true);
    }
}
