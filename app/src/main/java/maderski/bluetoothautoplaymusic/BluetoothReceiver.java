package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.Set;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private final String TAG = "BluetoothReceiver";
    private String btDevice = "None";

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

                if(BAPMPreferences.getBTDevices(context).contains(device.getName())) {
                    btDevice = device.getName();
                    waitingForBTA2dpOn(context, BAPMPreferences.getBTDevices(context).contains(device.getName()));
                }
                else {
                    btDevice = "Device NOT on List";
                }

                for(String cd : BAPMPreferences.getBTDevices(context)){
                    Log.i(TAG, "User selected device: " + cd);
                }
                Log.i(TAG, "Connected device: " + btDevice);
                Log.i(TAG, "OnConnect: isAUserSelectedBTDevice: " +
                        Boolean.toString(BAPMPreferences.getBTDevices(context).contains(device.getName())));
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                String disconnectedDevice = device.getName();
                Log.i(TAG, "Device disonnected: " + disconnectedDevice);

                Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " +
                        Boolean.toString(BAPMPreferences.getBTDevices(context).contains(device.getName())));

                if (BAPMPreferences.getBTDevices(context).contains(device.getName())) {
                    VariableStore.isBTConnected = false;
                    if(VariableStore.ranBTConnectPhoneDoStuff){
                        BluetoothActions.BTDisconnectPhoneDoStuff(context);
                    }
                }
                break;
        }
    }

    private void checksBeforeLaunch(Context context, Boolean isAUserSelectedBTDevice){
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);

        if (powerRequired && isAUserSelectedBTDevice) {
            if (Power.isPluggedIn(context) && VariableStore.isBTConnected) {
                BluetoothActions.BTConnectPhoneDoStuff(context);
            }
        } else if (!powerRequired && isAUserSelectedBTDevice && VariableStore.isBTConnected) {
            BluetoothActions.BTConnectPhoneDoStuff(context);
        }

    }

    private void waitingForBTA2dpOn(final Context context, final Boolean _isAUserSelectedBTDevice) {

        AudioFocus.getCurrentAudioFocus(context);

        //Try to releaseWakeLock() in case for some reason it was not released on disconnect
        ScreenONLock screenONLock = new ScreenONLock();
        screenONLock.releaseWakeLock();

        //Get original MediaVolume
        VolumeControl.originalMediaVolume = VariableStore.am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "Original Media Volume is: " + Integer.toString(VolumeControl.originalMediaVolume));

        //Start 10sec countdown checking for A2dp connection every second
        new CountDownTimer(30000,
                1000) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "A2dp Ready: " + Boolean.toString(VariableStore.am.isBluetoothA2dpOn()));
                if(VariableStore.am.isBluetoothA2dpOn()){
                    VariableStore.isBTConnected = true;
                    cancel();
                    checksBeforeLaunch(context, _isAUserSelectedBTDevice);
                }
            }

            public void onFinish() {
                Log.i(TAG, btDevice + " did NOT CONNECT via a2dp");
                VariableStore.isBTConnected = false;
            }
        }.start();
    }
}
