package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Set;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    public final static String TAG = "BluetoothReceiver";

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent)
    {
        //Toast.makeText(context, "Bluetooth Intent Received", Toast.LENGTH_SHORT).show();

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Set<String> BTDeviceList = BAPMPreferences.getBTDevices(context);

        //Get action that was broadcasted
        String action = intent.getAction();
        Log.d(TAG, "Bluetooth Intent Received: " + action);

        //Run if BTAudio is ready
        if(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equalsIgnoreCase(action) && BAPMPreferences.getBTDevices(context).contains(VariableStore.btDevice)){
            boolean powerRequired = BAPMPreferences.getPowerConnected(context);

            if(powerRequired){
                if(BluetoothActions.isBTAudioIsReady(intent))
                    VariableStore.isBTConnected = true;

                if(Power.isPluggedIn(context)){
                    if(BluetoothActions.isBTAudioIsReady(intent))
                        BluetoothActions.BTConnectPhoneDoStuff(context, VariableStore.btDevice);
                }else{
                    Log.i(TAG, "Power is required and NOT FOUND");
                }
            }else{
                if(BluetoothActions.isBTAudioIsReady(intent))
                    BluetoothActions.BTConnectPhoneDoStuff(context, VariableStore.btDevice);
            }
        }

        //Run on inital bluetooth connection
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equalsIgnoreCase(action))
        {
            VariableStore.btDevice = device.getName();

            Log.d(TAG, "Connected to " + VariableStore.btDevice);
            //Toast.makeText(context, "Connected to: " + btDevice, Toast.LENGTH_SHORT).show();
        }

        //Run on Bluetooth disconnect
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equalsIgnoreCase(action) && BAPMPreferences.getBTDevices(context).contains(VariableStore.btDevice)) {
            Log.d(TAG, "Disconnected from " + VariableStore.btDevice);
            //Toast.makeText(context, "Disconnected from: " + btDevice, Toast.LENGTH_SHORT).show();

            VariableStore.isBTConnected = false;

            if(VariableStore.ranBluetoothDoStuff) {
                BluetoothActions.BTDisconnectPhoneDoStuff(context);
                Log.i(TAG, "ranBluetoothDoStuff: " + Boolean.toString(VariableStore.ranBluetoothDoStuff));
            }
            else
                Log.i(TAG, "ranBluetoothDoStuff: " + Boolean.toString(VariableStore.ranBluetoothDoStuff));
        }

    }

}
