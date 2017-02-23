package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Interfaces.BluetoothState;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.BluetoothActions;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Notification;
import maderski.bluetoothautoplaymusic.PlayMusic;
import maderski.bluetoothautoplaymusic.Power;
import maderski.bluetoothautoplaymusic.Telephone;

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
        } else if(mIsSelectedBTDevice){
            onHeadphonesConnectSwitch(context);
        }
    }

    private void onHeadphonesConnectSwitch(final Context context){
        final AudioManager audioManager  = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        final PlayMusic playMusic = new PlayMusic(context);
        BAPMDataPreferences.setOriginalMediaVolume(context, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        Log.d(TAG, "Original Volume: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));
        switch(mAction) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, BAPMPreferences.getHeadphonePreferredVolume(context), 0);
                        playMusic.play();
                        playMusic.checkIfPlaying(context, 5);
                        BAPMDataPreferences.setIsHeadphonesDevice(context, true);
                        if(BuildConfig.DEBUG)
                            Toast.makeText(context, "Music Playing", Toast.LENGTH_SHORT).show();
                    }
                };
                handler.postDelayed(runnable, 5000);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                playMusic.pause();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, BAPMDataPreferences.getOriginalMediaVolume(context), 0);
                BAPMDataPreferences.setIsHeadphonesDevice(context, false);
                if(BuildConfig.DEBUG)
                    Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show();
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

//            case BluetoothDevice.ACTION_ACL_CONNECTED:
//                waitingForBTA2dpOn(context, mIsSelectedBTDevice, bluetoothActions, am);
//
//                if(BuildConfig.DEBUG) {
//                    for (String cd : BAPMPreferences.getBTDevices(context)) {
//                        Log.i(TAG, "User selected device: " + cd);
//                    }
//                    Log.i(TAG, "Connected device: " + mDevice);
//                    Log.i(TAG, "OnConnect: isAUserSelectedBTDevice: " + mIsSelectedBTDevice);
//                }
//                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "Device disconnected: " + mDevice.getName());

                    Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " +
                            Boolean.toString(mIsSelectedBTDevice));
                    Log.i(TAG, "Ran actionOnBTConnect: " + Boolean.toString(BAPMDataPreferences.getRanActionsOnBtConnect(context)));
                    Log.i(TAG, "LaunchNotifPresent: " + Boolean.toString(BAPMDataPreferences.getLaunchNotifPresent(context)));
                }

                sendIsSelectedBroadcast(context, false);

                if(BAPMDataPreferences.getRanActionsOnBtConnect(context)) {
                    mBluetoothActions.actionsOnBTDisconnect();
                }

                if(BAPMPreferences.getWaitTillOffPhone(context) && BAPMDataPreferences.getLaunchNotifPresent(context)){
                    Notification notification = new Notification();
                    notification.removeBAPMMessage(context);
                }
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
                Telephone telephone = new Telephone(context);

                if(!telephone.isOnCall()){
                    BAPMDataPreferences.setOriginalMediaVolume(context, am.getStreamVolume(AudioManager.STREAM_MUSIC));
                    Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));
                }

                mFirebaseHelper.connectViaA2DP(mDevice.getName(), true);
                checksBeforeLaunch(context, mIsSelectedBTDevice, am);
                break;
            case BluetoothProfile.STATE_DISCONNECTING:
                Log.d(TAG, "A2DP DISCONNECTING");
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                Log.d(TAG, "A2DP DISCONNECTED");
                break;
        }
    }

    private void checksBeforeLaunch(Context context, Boolean isAUserSelectedBTDevice, AudioManager am){
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);
        boolean isBTConnected = am.isBluetoothA2dpOn();

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
        BluetoothActions bluetoothActions = new BluetoothActions(context);
        bluetoothActions.actionsBTStateOff();
    }
}
