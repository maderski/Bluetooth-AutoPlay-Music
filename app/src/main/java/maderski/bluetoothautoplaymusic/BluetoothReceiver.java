package maderski.bluetoothautoplaymusic;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothReceiver.class.getName();

    private String action = "None";
    private BluetoothDevice device;
    private boolean isSelectedBTDevice = false;

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null) {
                isSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(device.getName());
                if(BuildConfig.DEBUG)
                    Log.d(TAG, "Connected device: " + device.getName() +
                            "\n" + "is SelectedBTDevice: " + Boolean.toString(isSelectedBTDevice));
            }

            if (intent.getAction() != null) {
                action = intent.getAction();
                selectedDevicePrepForActions(context);
            }
        }
    }

    private void selectedDevicePrepForActions(Context context){
        boolean isAHeadphonesBTDevice = BAPMPreferences.getHeadphoneDevices(context).contains(device.getName());
        if(BuildConfig.DEBUG)
            Log.d(TAG, "is A Headphone device: " + Boolean.toString(isAHeadphonesBTDevice));
        if (isSelectedBTDevice && !isAHeadphonesBTDevice) {
            if(!BAPMDataPreferences.getIsSelected(context)) {
                sendIsSelectedBroadcast(context, true);
            }
            bluetoothConnectDisconnectSwitch(context);
        } else if(isSelectedBTDevice){
            onHeadphonesConnectSwitch(context);
        }
    }

    private void onHeadphonesConnectSwitch(final Context context){
        final AudioManager audioManager  = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        final PlayMusic playMusic = new PlayMusic(context, audioManager);
        BAPMDataPreferences.setOriginalMediaVolume(context, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        if(BuildConfig.DEBUG)
            Log.d(TAG, "Original Volume: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));
        switch(action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, BAPMPreferences.getHeadphonePreferredVolume(context), 0);
                        playMusic.checkIfPlaying();
                        Toast.makeText(context, "Music Playing", Toast.LENGTH_SHORT).show();
                    }
                };
                handler.postDelayed(runnable, 5000);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                playMusic.pause();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, BAPMDataPreferences.getOriginalMediaVolume(context), 0);
                Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void bluetoothConnectDisconnectSwitch(Context context){

        ScreenONLock screenONLock = ScreenONLock.getInstance();
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        BluetoothActions bluetoothActions = new BluetoothActions(context, am,
                screenONLock, new Notification(), new VolumeControl(am));

        if(BuildConfig.DEBUG)
            Log.d(TAG, "Bluetooth Intent Received: " + action);

        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                waitingForBTA2dpOn(context, isSelectedBTDevice, bluetoothActions, am);

                if(BuildConfig.DEBUG) {
                    for (String cd : BAPMPreferences.getBTDevices(context)) {
                        Log.i(TAG, "User selected device: " + cd);
                    }
                    Log.i(TAG, "Connected device: " + device);
                    Log.i(TAG, "OnConnect: isAUserSelectedBTDevice: " + isSelectedBTDevice);
                }
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "Device disconnected: " + device.getName());

                    Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " +
                            Boolean.toString(isSelectedBTDevice));
                    Log.i(TAG, "Ran actionOnBTConnect: " + Boolean.toString(BAPMDataPreferences.getRanActionsOnBtConnect(context)));
                    Log.i(TAG, "LaunchNotifPresent: " + Boolean.toString(BAPMDataPreferences.getLaunchNotifPresent(context)));
                }

                sendIsSelectedBroadcast(context, false);

                if(BAPMDataPreferences.getRanActionsOnBtConnect(context))
                    bluetoothActions.actionsOnBTDisconnect();

                if(BAPMPreferences.getWaitTillOffPhone(context) && BAPMDataPreferences.getLaunchNotifPresent(context)){
                    Notification notification = new Notification();
                    notification.removeBAPMMessage(context);
                }
                break;
        }
    }

    private void checksBeforeLaunch(Context context, Boolean isAUserSelectedBTDevice,
                                    BluetoothActions bluetoothActions, AudioManager am){
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);
        boolean isBTConnected = am.isBluetoothA2dpOn();

        if (powerRequired && isAUserSelectedBTDevice) {
            if (Power.isPluggedIn(context) && isBTConnected) {
                bluetoothActions.OnBTConnect();
            }
        } else if (!powerRequired && isAUserSelectedBTDevice && isBTConnected) {
            bluetoothActions.OnBTConnect();
        }

    }

    private void waitingForBTA2dpOn(final Context context, final Boolean _isAUserSelectedBTDevice,
                                    final BluetoothActions bluetoothActions, final AudioManager am) {
        Telephone telephone = new Telephone(context);

        if(!telephone.isOnCall()){
            BAPMDataPreferences.setOriginalMediaVolume(context, am.getStreamVolume(AudioManager.STREAM_MUSIC));
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));
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
                    checksBeforeLaunch(context, _isAUserSelectedBTDevice, bluetoothActions, am);
                }
            }

            public void onFinish() {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "BTDevice did NOT CONNECT via a2dp");
            }
        }.start();
    }

    private void sendIsSelectedBroadcast(Context context, boolean isSelected){
        Intent isSelectedIntent = new Intent();
        isSelectedIntent.putExtra("isSelected", isSelected);
        isSelectedIntent.setAction("maderski.bluetoothautoplaymusic.isselected");
        context.sendBroadcast(isSelectedIntent);
    }
}
