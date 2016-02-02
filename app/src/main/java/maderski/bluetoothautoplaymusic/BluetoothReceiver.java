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
    private ScreenONLock screenONLock = new ScreenONLock();
    private RingerControl ringerControl = new RingerControl();

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "Bluetooth Intent Received");
        //Toast.makeText(context, "Bluetooth Intent Received", Toast.LENGTH_SHORT).show();

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Set<String> BTDeviceList = BAPMPreferences.getBTDevices(context);

        //Get action that was broadcasted
        String action = intent.getAction();

        //Run if BTAudio is ready
        if(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equalsIgnoreCase(action)){
            if(isBTAudioIsReady(intent))
                BTConnectPhoneDoStuff(context);
        }

        //Run on inital bluetooth connection
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equalsIgnoreCase(action))
        {
            String btDevice = device.getName();

            Log.d(TAG, "Connected to " + btDevice);
            //Toast.makeText(context, "Connected to: " + btDevice, Toast.LENGTH_SHORT).show();

            if(BTDeviceList.contains(btDevice)) {
                Log.i(btDevice, " found");
                VariableStore.btDevice = btDevice;
            }
        }

        //Run on Bluetooth disconnect
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equalsIgnoreCase(action))
        {
            String btDevice = device.getName();
            
            Log.d(TAG, "Disconnected from " + btDevice);
            //Toast.makeText(context, "Disconnected from: " + btDevice, Toast.LENGTH_SHORT).show();

            if(BTDeviceList.contains(btDevice)) {
                Log.i(btDevice, " found");

                BTDisconnectPhoneDoStuff(context);
            }
        }

    }

    //Return true if Bluetooth Audio is ready
    private boolean isBTAudioIsReady(Intent intent){
        boolean ready = false;
        int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
        if(state == BluetoothA2dp.STATE_CONNECTED) {
            Log.e(TAG, "CONNECTED!!! :D");
            ready = true;
        }

        return ready;
    }

    //Creates notification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    private void BTConnectPhoneDoStuff(Context context){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);

        Notification.BAPMMessage(context);

        if(screenON){
            screenONLock.enableWakeLock(context);
        }

        if(priorityMode){
            ringerControl.soundsOFF(context);
        }

        if(volumeMAX){
            ringerControl.volumeMAX(context);
        }

        if(unlockScreen){
            launchMainActivity(context);
        }

        try {
            LaunchApp.launchSelectedMusicPlayer(context);
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }

        if(!launchMusicPlayer){
            LaunchApp.delayLaunchMaps(context, 2);
        }

    }

    //Removes notification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music and abandons AudioFocus
    private void BTDisconnectPhoneDoStuff(Context context){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);

        Notification.removeBAPMMessage(context);

        if(screenON){
            screenONLock.releaseWakeLock(context);
        }

        if(priorityMode){
            ringerControl.soundsON(context);
        }

        if(launchMusicPlayer) {
            PlayMusic.pause();
            AudioFocus.abandonAudioFocus();
        }
    }

    //Launch MainActivity, used for unlocking the screen
    private void launchMainActivity(Context context){
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
