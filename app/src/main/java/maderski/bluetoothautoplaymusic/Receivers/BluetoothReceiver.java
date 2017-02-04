package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.BluetoothDeviceHelper;
import maderski.bluetoothautoplaymusic.Interfaces.BluetoothState;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.BluetoothActions;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Notification;
import maderski.bluetoothautoplaymusic.PlayMusic;
import maderski.bluetoothautoplaymusic.Power;
import maderski.bluetoothautoplaymusic.ScreenONLock;
import maderski.bluetoothautoplaymusic.Telephone;
import maderski.bluetoothautoplaymusic.VolumeControl;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver implements BluetoothState {

    private static final String TAG = BluetoothReceiver.class.getName();

    private String mAction = "None";
    private BluetoothDevice mDevice;
    private boolean mIsSelectedBTDevice = false;
    private FirebaseHelper mFirebaseHelper;

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (mDevice != null) {
                mIsSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(mDevice.getName());
                if(BuildConfig.DEBUG)
                    Log.d(TAG, "Connected device: " + mDevice.getName() +
                            "\n" + "is SelectedBTDevice: " + Boolean.toString(mIsSelectedBTDevice));
            }

            if (intent.getAction() != null) {
                mAction = intent.getAction();
                mFirebaseHelper = new FirebaseHelper(context);
                selectedDevicePrepForActions(context);
            }
        }
    }

    private void selectedDevicePrepForActions(Context context){
        boolean isAHeadphonesBTDevice = BAPMPreferences.getHeadphoneDevices(context).contains(mDevice.getName());
        if(BuildConfig.DEBUG)
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
        final PlayMusic playMusic = new PlayMusic(context, audioManager);
        BAPMDataPreferences.setOriginalMediaVolume(context, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        if(BuildConfig.DEBUG)
            Log.d(TAG, "Original Volume: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));
        switch(mAction) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, BAPMPreferences.getHeadphonePreferredVolume(context), 0);
                        playMusic.checkIfPlaying(5);
                        if(BuildConfig.DEBUG)
                            Toast.makeText(context, "Music Playing", Toast.LENGTH_SHORT).show();
                    }
                };
                handler.postDelayed(runnable, 5000);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                playMusic.pause();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, BAPMDataPreferences.getOriginalMediaVolume(context), 0);
                if(BuildConfig.DEBUG)
                    Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void bluetoothConnectDisconnectSwitch(Context context){

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        BluetoothActions bluetoothActions = new BluetoothActions(context, am);

        if(BuildConfig.DEBUG)
            Log.d(TAG, "Bluetooth Intent Received: " + mAction);

        switch (mAction) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                waitingForBTA2dpOn(context, mIsSelectedBTDevice, bluetoothActions, am);

                if(BuildConfig.DEBUG) {
                    for (String cd : BAPMPreferences.getBTDevices(context)) {
                        Log.i(TAG, "User selected device: " + cd);
                    }
                    Log.i(TAG, "Connected device: " + mDevice);
                    Log.i(TAG, "OnConnect: isAUserSelectedBTDevice: " + mIsSelectedBTDevice);
                }
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "Device disconnected: " + mDevice.getName());

                    Log.i(TAG, "OnDisconnect: isAUserSelectedBTDevice: " +
                            Boolean.toString(mIsSelectedBTDevice));
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
                    mFirebaseHelper.connectViaA2DP(mDevice.getName(), true);
                    checksBeforeLaunch(context, _isAUserSelectedBTDevice, bluetoothActions, am);
                }
            }

            public void onFinish() {
                mFirebaseHelper.connectViaA2DP(mDevice.getName(), false);
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

    @Override
    public void adapterOff(Context context) {
        // Set volume back
        final AudioManager audioManager  = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        BluetoothActions bluetoothActions = new BluetoothActions(context, audioManager);
        bluetoothActions.actionsBTStateOff();
    }
}
