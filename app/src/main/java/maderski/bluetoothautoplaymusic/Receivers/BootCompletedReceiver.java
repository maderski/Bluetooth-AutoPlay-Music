package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Services.jobservices.StartBAPMServiceJobService;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 1/5/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedReceiver.class.getName();

    //Start BAPMService on phone boot
    public void onReceive(Context context, Intent intent) {
        // Schedule Job to run on boot
        ServiceUtils.scheduleJob(context, StartBAPMServiceJobService.class);

        // Check to see Bluetooth is enabled and if not enable it
        checkIfBluetoothEnabled();

        Log.d(TAG, "BAPM Service Started");
    }

    private void checkIfBluetoothEnabled(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }
}
