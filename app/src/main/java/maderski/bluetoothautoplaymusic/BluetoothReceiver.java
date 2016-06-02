package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private final String TAG = "BluetoothReceiver";

    private String btDevice = "None";
    private AudioManager am;

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Bluetooth Intent Received", Toast.LENGTH_SHORT).show();

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        boolean isSelectedBTDevice = false;
        //Get action that was broadcasted
        String action = "None";

        if(intent != null)
            if(intent.getAction() != null)
                action = intent.getAction();
        Log.d(TAG, "Bluetooth Intent Received: " + action);

        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                isSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(device.getName());
                if(isSelectedBTDevice) {
                    btDevice = device.getName();
                    am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    waitingForBTA2dpOn(context, isSelectedBTDevice);
                }
                else {
                    btDevice = "Device NOT on List";
                }

                for(String cd : BAPMPreferences.getBTDevices(context)){
                    Log.i(TAG, "User selected device: " + cd);
                }
                Log.i(TAG, "Connected device: " + btDevice);
                Log.i(TAG, "OnConnect: isAUserSelectedBTDevice: " + isSelectedBTDevice);
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                isSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(device.getName());
                Log.i(TAG, "Device disonnected: " + device.getName());

                Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " +
                        Boolean.toString(isSelectedBTDevice));

                if (isSelectedBTDevice && BluetoothActions.getRanBTConnectPhoneDoStuff()) {
                    am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    BluetoothActions bluetoothActions = new BluetoothActions();
                    bluetoothActions.BTDisconnectDoStuff(context, am);
                }
                break;

            case Intent.ACTION_POWER_CONNECTED:
                am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

                boolean isBTConnected = am.isBluetoothA2dpOn();
                Log.i(TAG, "Power Connected");
                Log.i(TAG, "Is BT Connected: " + Boolean.toString(isBTConnected));
                //Toast.makeText(context, "BAPM Power Connected", Toast.LENGTH_SHORT).show();
                boolean powerRequired = BAPMPreferences.getPowerConnected(context);

                if(powerRequired && isBTConnected && !BluetoothActions.getRanBTConnectPhoneDoStuff()){
                    //Toast.makeText(context, "BTAudioPWR Launch", Toast.LENGTH_SHORT).show();
                    BluetoothActions bluetoothActions = new BluetoothActions();
                    bluetoothActions.OnBTConnect(context, am);
                }
                break;
        }
    }

    private void checksBeforeLaunch(Context context, Boolean isAUserSelectedBTDevice){
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);
        boolean isBTConnected = am.isBluetoothA2dpOn();

        if (powerRequired && isAUserSelectedBTDevice) {
            if (Power.isPluggedIn(context) && isBTConnected) {
                BluetoothActions bluetoothActions = new BluetoothActions();
                bluetoothActions.OnBTConnect(context, am);
            }
        } else if (!powerRequired && isAUserSelectedBTDevice && isBTConnected) {
            BluetoothActions bluetoothActions = new BluetoothActions();
            bluetoothActions.OnBTConnect(context, am);
        }

    }

    private void waitingForBTA2dpOn(final Context context, final Boolean _isAUserSelectedBTDevice) {

        //Try to releaseWakeLock() in case for some reason it was not released on disconnect
        ScreenONLock screenONLock = new ScreenONLock();
        screenONLock.releaseWakeLock();

        //Get original MediaVolume if not on a call
        Telephone telephone = new Telephone(context);
        if(!telephone.isOnCall()) {
            VolumeControl.originalMediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.i(TAG, "Original Media Volume is: " + Integer.toString(VolumeControl.originalMediaVolume));
        }

        //Start 10sec countdown checking for A2dp connection every second
        new CountDownTimer(30000,
                1000) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "A2dp Ready: " + Boolean.toString(am.isBluetoothA2dpOn()));
                if(am.isBluetoothA2dpOn()){
                    cancel();
                    checksBeforeLaunch(context, _isAUserSelectedBTDevice);
                }
            }

            public void onFinish() {
                Log.i(TAG, btDevice + " did NOT CONNECT via a2dp");
            }
        }.start();
    }
}
