package maderski.bluetoothautoplaymusic.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.services.BTDisconnectService;
import maderski.bluetoothautoplaymusic.services.BTStateChangedService;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

/**
 * Created by Jason on 1/28/17.
 */

public class BTStateChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "BTStateChangedReceiver";

    private String mAction;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            if(intent.getAction() != null) {
                mAction = intent.getAction();
                connectionStateChangedActions(context, intent);
            }
        }
    }

    private void connectionStateChangedActions(Context context, Intent intent){
        if (mAction.equalsIgnoreCase(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "Bluetooth off");
                    ServiceUtils.stopService(context, BTStateChangedService.class, BTStateChangedService.TAG);
                    ServiceUtils.startService(context, BTDisconnectService.class, BTDisconnectService.TAG);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "Turning Bluetooth off...");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "Bluetooth on");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d(TAG, "Turning Bluetooth on...");
                    break;
                }
            }
    }
}
