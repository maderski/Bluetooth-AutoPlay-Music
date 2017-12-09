package maderski.bluetoothautoplaymusic.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.Receivers.BTStateChangedReceiver;
import maderski.bluetoothautoplaymusic.UI.activities.MainActivity;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 6/6/17.
 */

public class BTStateChangedService extends Service {
    public static final String TAG = "BTStateChangedService";

    private final BTStateChangedReceiver mBtStateChangedReceiver = new BTStateChangedReceiver();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "START BT STATE CHANGED RECEIVER");
        IntentFilter btStateChangedFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(mBtStateChangedReceiver, btStateChangedFilter);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String title = getString(R.string.listening_if_the_phones_BT_off);
            String message = getString(R.string.app_name);
            ServiceUtils.createServiceNotification(3452, title, message, this, null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop receivers
        Log.d(TAG, "STOP BT STATE CHANGED RECEIVER");
        unregisterReceiver(mBtStateChangedReceiver);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            stopForeground(true);
        }
    }
}
