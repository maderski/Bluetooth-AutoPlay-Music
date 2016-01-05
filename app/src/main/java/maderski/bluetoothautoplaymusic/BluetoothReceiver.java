package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {
    public final static String TAG = "BluetoothReceiver";
    private String BTDevice = "SB517";

    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "Bluetooth Intent Received");
        //Toast.makeText(context, "Bluetooth Intent Received", Toast.LENGTH_SHORT).show();

        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equalsIgnoreCase(action))
        {
            String btDevice = device.getName();
            Log.d(TAG, "Connected to " + btDevice);
            Toast.makeText(context, "Connected to: " + btDevice, Toast.LENGTH_SHORT).show();

            if(btDevice.equals(BTDevice)) {
                Log.i(btDevice, " found");


                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }

        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equalsIgnoreCase(action))
        {
            String btDevice = device.getName();
            Log.d(TAG, "Disconnected from " + btDevice);
            Toast.makeText(context, "Disconnected from: " + btDevice, Toast.LENGTH_SHORT).show();

            if(btDevice.equals(BTDevice)) {
                Log.i(btDevice, " found");

            }
        }
    }

}
