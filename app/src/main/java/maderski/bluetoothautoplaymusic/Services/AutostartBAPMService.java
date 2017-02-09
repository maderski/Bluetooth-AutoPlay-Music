package maderski.bluetoothautoplaymusic.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.BuildConfig;

/**
 * Created by Jason on 1/5/16.
 */
public class AutostartBAPMService extends BroadcastReceiver {

    private static final String TAG = AutostartBAPMService.class.getName();

    //Start BAPMService on phone boot
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BAPMService.class);
        context.startService(serviceIntent);

        Log.d(TAG, "BAPM Service Started");
    }
}
