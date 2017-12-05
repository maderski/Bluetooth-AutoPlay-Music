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

import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.Receivers.BTStateChangedReceiver;
import maderski.bluetoothautoplaymusic.Receivers.CustomReceiver;
import maderski.bluetoothautoplaymusic.Receivers.PowerReceiver;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.UI.activities.MainActivity;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 6/6/17.
 */

public class OnBTConnectService extends Service {
    public static final String TAG = "OnBTConnectService";

    private final PowerReceiver mPowerReceiver = new PowerReceiver();
    private final CustomReceiver mCustomReceiver = new CustomReceiver();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start receivers
        if(BAPMPreferences.getPowerConnected(this)) {
            Log.d(TAG, "START POWER RECEIVER");
            IntentFilter powerFilter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
            registerReceiver(mPowerReceiver, powerFilter);
        }

        if(BAPMPreferences.getWaitTillOffPhone(this) || BAPMPreferences.getPowerConnected(this)) {
            Log.d(TAG, "START CUSTOM RECEIVER");
            IntentFilter customFilter = new IntentFilter();
            customFilter.addAction("maderski.bluetoothautoplaymusic.pluggedinlaunch");
            customFilter.addAction("maderski.bluetoothautoplaymusic.offtelephonelaunch");
            registerReceiver(mCustomReceiver, customFilter);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String title = getString(R.string.connect_message);
        String message = getString(R.string.app_name);
        ServiceUtils.createServiceNotification(3451, title, message, this);
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
        if (BAPMPreferences.getPowerConnected(this)) {
            Log.d(TAG, "STOP POWER RECEIVER");
            unregisterReceiver(mPowerReceiver);
        }

        if (BAPMPreferences.getWaitTillOffPhone(this) || BAPMPreferences.getPowerConnected(this)) {
            Log.d(TAG, "STOP CUSTOM RECEIVER");
            unregisterReceiver(mCustomReceiver);
        }

        stopForeground(true);
    }
}
