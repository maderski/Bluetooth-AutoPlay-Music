package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.Set;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    public final static String TAG = "BluetoothReceiver";

    private static boolean IsAUserSelectedBTDevice = false;

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Bluetooth Intent Received", Toast.LENGTH_SHORT).show();

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Set<String> BTDeviceList = BAPMPreferences.getBTDevices(context);

        //Get action that was broadcasted
        String action = "None";

        if(intent != null)
            if(intent.getAction() != null)
                action = intent.getAction();
        Log.d(TAG, "Bluetooth Intent Received: " + action);

        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                String connectedBTDevice = device.getName();
                IsAUserSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(connectedBTDevice);
                if(IsAUserSelectedBTDevice) {
                    VariableStore.btDevice = connectedBTDevice;
                    waitingForBTA2dpOn(context);
                }
                else {
                    VariableStore.btDevice = "Device NOT on List";
                }

                for(String cd : BAPMPreferences.getBTDevices(context)){
                    Log.i(TAG, "User selected device: " + cd);
                }
                Log.i(TAG, "Connected device: " + VariableStore.btDevice);
                Log.i(TAG, "OnConnect: isAUserSelectedBTDevice: " + Boolean.toString(IsAUserSelectedBTDevice));
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                String disconnectedDevice = device.getName();
                Log.i(TAG, "Device disonnected: " + disconnectedDevice);

                IsAUserSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(disconnectedDevice);
                Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " + Boolean.toString(IsAUserSelectedBTDevice));

                if (IsAUserSelectedBTDevice) {
                    VariableStore.isBTConnected = false;
                    if(VariableStore.ranBTConnectPhoneDoStuff){
                        BluetoothActions.BTDisconnectPhoneDoStuff(context);
                    }
                }
                break;
        }
    }

    private void checksBeforeLaunch(Context context){
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);

        if (powerRequired && IsAUserSelectedBTDevice) {
            if (Power.isPluggedIn(context) && VariableStore.isBTConnected) {
                BluetoothActions.BTConnectPhoneDoStuff(context, VariableStore.btDevice);
            }
        } else if (!powerRequired && IsAUserSelectedBTDevice && VariableStore.isBTConnected) {
            BluetoothActions.BTConnectPhoneDoStuff(context, VariableStore.btDevice);
        }

    }

    private void waitingForBTA2dpOn(Context context) {
        final Context ctx = context;

        AudioFocus.getCurrentAudioFocus(context);

        new CountDownTimer(30000,
                1000) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "On Tick A2dp Ready: " + Boolean.toString(VariableStore.am.isBluetoothA2dpOn()));
                if(VariableStore.am.isBluetoothA2dpOn()){
                    VariableStore.isBTConnected = true;
                    cancel();
                    checksBeforeLaunch(ctx);
                }
            }

            public void onFinish() {
                Log.i(TAG, VariableStore.btDevice + " did NOT CONNECT via a2dp");
                VariableStore.isBTConnected = false;
            }
        }.start();
    }
}
