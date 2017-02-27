package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Interfaces.BluetoothState;

/**
 * Created by Jason on 1/28/17.
 */

public class BTStateChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "BTStateChangedReceiver";

    private String mAction;
    private BluetoothState mBluetoothState;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            if(intent.getAction() != null) {
                mAction = intent.getAction();
                mBluetoothState = new BluetoothReceiver();
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
                    mBluetoothState.adapterOff(context);
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
