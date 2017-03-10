package maderski.bluetoothautoplaymusic.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Services.BAPMService;

/**
 * Created by Jason on 1/5/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedReceiver.class.getName();

    //Start BAPMService on phone boot
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BAPMService.class);
        context.startService(serviceIntent);

        Log.d(TAG, "BAPM Service Started");
    }
}
