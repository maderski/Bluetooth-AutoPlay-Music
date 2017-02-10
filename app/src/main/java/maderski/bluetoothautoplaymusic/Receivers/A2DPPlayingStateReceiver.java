package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.PlayMusic;

/**
 * Created by Jason on 2/4/17.
 */

public class A2DPPlayingStateReceiver extends BroadcastReceiver {
    private static final String TAG = "a2dpPlayingStateReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            if(intent.getAction() != null){
                String action = intent.getAction();
                if(action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);
                    switch (state){
                        case BluetoothA2dp.STATE_PLAYING:
                            Log.d(TAG, "A2DP STATE PLAYING");
                            break;
                        case BluetoothA2dp.STATE_NOT_PLAYING:
                            Log.d(TAG, "A2DP STATE NOT PLAYING");
                            break;
                    }
                }
            }
        }
    }
}
