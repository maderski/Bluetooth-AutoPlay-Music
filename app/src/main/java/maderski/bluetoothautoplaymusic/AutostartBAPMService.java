package maderski.bluetoothautoplaymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jason on 1/5/16.
 */
public class AutostartBAPMService extends BroadcastReceiver {

    private String TAG = AutostartBAPMService.class.getName();

    //Start BAPMService on phone boot
    public void onReceive(Context arg0, Intent argl) {
        Intent intent = new Intent(arg0, BAPMService.class);
        arg0.startService(intent);
        Log.i(TAG, "BAPM Service Started");
    }
}
