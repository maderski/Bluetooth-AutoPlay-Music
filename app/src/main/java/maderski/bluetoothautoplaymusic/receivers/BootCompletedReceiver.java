package maderski.bluetoothautoplaymusic.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.services.jobservices.StartBAPMServiceJobService;
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

/**
 * Created by Jason on 1/5/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedReceiver.class.getName();

    //Start BAPMService on phone boot
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                // Schedule Job to run on boot
                ServiceUtils.INSTANCE.scheduleJob(context, StartBAPMServiceJobService.class);

                // Check to see Bluetooth is disabled and if it is disabled, then enable it
                BluetoothUtils.INSTANCE.enableDisabledBluetooth();

                Log.d(TAG, "BAPM Service Started");
            }
        }
    }
}
