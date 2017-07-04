package maderski.bluetoothautoplaymusic.Services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Receivers.BTStateChangedReceiver;

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
    }
}
