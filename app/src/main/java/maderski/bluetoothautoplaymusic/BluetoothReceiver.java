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

    private static final String TAG = "BluetoothReceiver";

    private String btDevice = "None";
    private AudioManager am;
    private BluetoothActions bluetoothActions;
    private ScreenONLock screenONLock;
    private Notification notification;

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        boolean isSelectedBTDevice = false;
        String action = "None";
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if(intent != null)
            if(intent.getAction() != null){
                action = intent.getAction();
                isSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(device.getName());
                PowerReceiver.selectedBTDevice = isSelectedBTDevice;
                if(isSelectedBTDevice) {
                    am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    screenONLock = new ScreenONLock();
                    notification = new Notification();
                    bluetoothActions = new BluetoothActions(context, am, screenONLock, notification);
                }
            }

        if(BuildConfig.DEBUG)
            Log.d(TAG, "Bluetooth Intent Received: " + action);

        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                if(isSelectedBTDevice) {
                    btDevice = device.getName();
                    waitingForBTA2dpOn(context, isSelectedBTDevice);
                }
                else {
                    btDevice = "Device NOT on List";
                }
                if(BuildConfig.DEBUG) {
                    for (String cd : BAPMPreferences.getBTDevices(context)) {
                        Log.i(TAG, "User selected device: " + cd);
                    }
                    Log.i(TAG, "Connected device: " + btDevice);
                    Log.i(TAG, "OnConnect: isAUserSelectedBTDevice: " + isSelectedBTDevice);
                }
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "Device disonnected: " + device.getName());

                    Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " +
                            Boolean.toString(isSelectedBTDevice));
                }

                if (isSelectedBTDevice && BluetoothActions.getRanActionsOnBTConnect()) {
                    bluetoothActions.actionsOnBTDisconnect();
                }else if(BAPMPreferences.getWaitTillOffPhone(context) && notification.launchNotifPresent){
                    notification.removeBAPMMessage(context);
                }
                break;
        }
    }

    private void checksBeforeLaunch(Context context, Boolean isAUserSelectedBTDevice){
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);
        boolean isBTConnected = am.isBluetoothA2dpOn();

        if (powerRequired && isAUserSelectedBTDevice) {
            if (Power.isPluggedIn(context) && isBTConnected) {
                bluetoothActions.OnBTConnect();
            }else{
                PowerReceiver.bluetoothActions = bluetoothActions;
            }
        } else if (!powerRequired && isAUserSelectedBTDevice && isBTConnected) {
            bluetoothActions.OnBTConnect();
        }

    }

    private void waitingForBTA2dpOn(final Context context, final Boolean _isAUserSelectedBTDevice) {

        //Try to releaseWakeLock() in case for some reason it was not released on disconnect
        screenONLock.releaseWakeLock();

        Telephone telephone = new Telephone(context);

        if(!telephone.isOnCall()){
            VolumeControl.originalMediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Original Media Volume is: " + Integer.toString(VolumeControl.originalMediaVolume));
        }

        //Start 30sec countdown checking for A2dp connection every second
        new CountDownTimer(30000,
                1000)
        {
            public void onTick(long millisUntilFinished) {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "A2dp Ready: " + Boolean.toString(am.isBluetoothA2dpOn()));
                if(am.isBluetoothA2dpOn()){
                    cancel();
                    checksBeforeLaunch(context, _isAUserSelectedBTDevice);
                }
            }

            public void onFinish() {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, btDevice + " did NOT CONNECT via a2dp");
            }
        }.start();
    }
}
