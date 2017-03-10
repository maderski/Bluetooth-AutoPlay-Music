package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.tv.TvContract;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Helpers.ReceiverHelper;
import maderski.bluetoothautoplaymusic.Interfaces.BluetoothState;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.BluetoothActions;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Notification;
import maderski.bluetoothautoplaymusic.Controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.Power;
import maderski.bluetoothautoplaymusic.Telephone;
import maderski.bluetoothautoplaymusic.Controls.VolumeControl;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver implements BluetoothState {

    private static final String TAG = BluetoothReceiver.class.getName();

    private String mAction = "None";
    private BluetoothDevice mDevice;
    private boolean mIsSelectedBTDevice = false;
    private BluetoothActions mBluetoothActions;
    private FirebaseHelper mFirebaseHelper;
    private Intent mIntent;

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (mDevice != null && intent.getAction() != null) {
                mIsSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(mDevice.getName());
                Log.d(TAG, "Connected device: " + mDevice.getName() +
                            "\n" + "is SelectedBTDevice: " + Boolean.toString(mIsSelectedBTDevice));

                mAction = intent.getAction();
                mIntent = intent;
                Log.d(TAG, "ACTION: " + mAction);
                mFirebaseHelper = new FirebaseHelper(context);
                selectedDevicePrepForActions(context);
            }
        }
    }

    private void selectedDevicePrepForActions(Context context){
        boolean isAHeadphonesBTDevice = BAPMPreferences.getHeadphoneDevices(context).contains(mDevice.getName());
        Log.d(TAG, "is A Headphone device: " + Boolean.toString(isAHeadphonesBTDevice));
        if (mIsSelectedBTDevice && !isAHeadphonesBTDevice) {
            if(!BAPMDataPreferences.getIsSelected(context)) {
                sendIsSelectedBroadcast(context, true);
            }
            bluetoothConnectDisconnectSwitch(context);
        } else if(isAHeadphonesBTDevice){
            onHeadphonesConnectSwitch(context);
        }
    }

    private void onHeadphonesConnectSwitch(final Context context){
        final AudioManager audioManager  = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        final PlayMusicControl playMusicControl = new PlayMusicControl(context);
        final VolumeControl volumeControl = new VolumeControl(context);
        switch(mAction) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                volumeControl.saveOriginalVolume();
                Log.d(TAG, "Original Volume: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Get headphone preferred volume
                        int preferredVolume = BAPMPreferences.getHeadphonePreferredVolume(context);
                        // Set headphone preferred volume
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, preferredVolume, 0);
                        // Play music
                        playMusicControl.play();
                        // Start checking if music is playing
                        playMusicControl.checkIfPlaying(context, 5);
                        Log.d(TAG, "HEADPHONE VOLUME SET TO:" + Integer.toString(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

                        BAPMDataPreferences.setIsHeadphonesDevice(context, true);
                        if(BuildConfig.DEBUG)
                            Toast.makeText(context, "Music Playing", Toast.LENGTH_SHORT).show();
                    }
                };
                handler.postDelayed(runnable, 5000);

                ReceiverHelper.startReceiver(context, BTStateChangedReceiver.class);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                playMusicControl.pause();
                PlayMusicControl.cancelCheckIfPlaying();
                if(audioManager.isMusicActive()) {
                    playMusicControl.pause();
                }
                volumeControl.checkSetOriginalVolume(4);

                BAPMDataPreferences.setIsHeadphonesDevice(context, false);
                if(BuildConfig.DEBUG)
                    Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show();

                ReceiverHelper.stopReceiver(context, BTStateChangedReceiver.class);
                break;
        }
    }

    private void bluetoothConnectDisconnectSwitch(Context context){
        boolean isHeadphones = BAPMDataPreferences.getIsAHeadphonesDevice(context);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(mBluetoothActions == null) {
            mBluetoothActions = new BluetoothActions(context);
        }

        if(isHeadphones){
            BAPMDataPreferences.setIsHeadphonesDevice(context, false);
        }

        Log.d(TAG, "Bluetooth Intent Received: " + mAction);

        switch (mAction) {
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                a2dpAction(context, am);
                break;
        }
    }

    private void a2dpAction(final Context context, final AudioManager am) {
        final int state = mIntent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);
        switch (state) {
            case BluetoothProfile.STATE_CONNECTING:
                Log.d(TAG, "A2DP CONNECTING");
                break;
            case BluetoothProfile.STATE_CONNECTED:
                Log.d(TAG, "A2DP CONNECTED");
                mFirebaseHelper.connectViaA2DP(mDevice.getName(), true);
                checkForWifiTurnOffDevice(context, true);
                checksBeforeLaunch(context, mIsSelectedBTDevice, am);
                ReceiverHelper.startReceiver(context, CustomReceiver.class);
                ReceiverHelper.startReceiver(context, BTStateChangedReceiver.class);
                break;
            case BluetoothProfile.STATE_DISCONNECTING:
                Log.d(TAG, "A2DP DISCONNECTING");
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                Log.d(TAG, "A2DP DISCONNECTED");
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "Device disconnected: " + mDevice.getName());

                    Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " +
                            Boolean.toString(mIsSelectedBTDevice));
                    Log.i(TAG, "Ran actionOnBTConnect: " + Boolean.toString(BAPMDataPreferences.getRanActionsOnBtConnect(context)));
                    Log.i(TAG, "LaunchNotifPresent: " + Boolean.toString(BAPMDataPreferences.getLaunchNotifPresent(context)));
                }

                sendIsSelectedBroadcast(context, false);

                if(BAPMDataPreferences.getRanActionsOnBtConnect(context)) {
                    PlayMusicControl.cancelCheckIfPlaying();
                    mBluetoothActions.actionsOnBTDisconnect();
                }

                if(BAPMPreferences.getWaitTillOffPhone(context) && BAPMDataPreferences.getLaunchNotifPresent(context)){
                    Notification notification = new Notification();
                    notification.removeBAPMMessage(context);
                }

                if(!BAPMDataPreferences.getRanActionsOnBtConnect(context)){
                    checkForWifiTurnOffDevice(context, false);
                }
                ReceiverHelper.stopReceiver(context, CustomReceiver.class);
                ReceiverHelper.stopReceiver(context, BTStateChangedReceiver.class);
                break;
        }
    }

    private void checksBeforeLaunch(Context context, Boolean isAUserSelectedBTDevice, AudioManager am){
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);
        boolean isBTConnected = am.isBluetoothA2dpOn();

        VolumeControl volumeControl = new VolumeControl(context);
        Telephone telephone = new Telephone(context);

        if(!telephone.isOnCall()){
            volumeControl.saveOriginalVolume();
            Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));
        }

        if (powerRequired && isAUserSelectedBTDevice) {
            if (Power.isPluggedIn(context) && isBTConnected) {
                mBluetoothActions.OnBTConnect();
            }
        } else if (!powerRequired && isAUserSelectedBTDevice && isBTConnected) {
            mBluetoothActions.OnBTConnect();
        }

    }

    private void sendIsSelectedBroadcast(Context context, boolean isSelected){
        Intent isSelectedIntent = new Intent();
        isSelectedIntent.putExtra("isSelected", isSelected);
        isSelectedIntent.setAction("maderski.bluetoothautoplaymusic.isselected");
        context.sendBroadcast(isSelectedIntent);
    }

    @Override
    public void adapterOff(Context context) {
        ReceiverHelper.stopReceiver(context, BTStateChangedReceiver.class);
        BluetoothActions bluetoothActions = new BluetoothActions(context);
        bluetoothActions.actionsBTStateOff();
    }

    private void checkForWifiTurnOffDevice(Context context, boolean isConnected){
        if(mDevice != null) {
            if (BAPMPreferences.getTurnWifiOffDevices(context).contains(mDevice.getName())) {
                BAPMDataPreferences.setIsTurnOffWifiDevice(context, isConnected);
                Log.d(TAG, "TURN OFF WIFI DEVICE SET TO: " + Boolean.toString(isConnected));
            }
        }
    }
}
