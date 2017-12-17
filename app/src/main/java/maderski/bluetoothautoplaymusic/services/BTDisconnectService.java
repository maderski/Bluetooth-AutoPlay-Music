package maderski.bluetoothautoplaymusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import maderski.bluetoothautoplaymusic.bluetoothactions.BTDisconnectActions;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

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
        String title = getString(R.string.disconnect_message);
        String message = getString(R.string.app_name);
        ServiceUtils.createServiceNotification(3454, title, message, this, null);
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
