package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 2/22/16.
 */
public class BluetoothActions {

    final static String TAG = BluetoothActions.class.getName();

    private static ScreenONLock screenONLock = new ScreenONLock();
    private static int currentRingerSet;

    //Return true if Bluetooth Audio is ready
    public static boolean isBTAudioIsReady(Intent intent){
        boolean ready = false;
        int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
        if(state == BluetoothA2dp.STATE_CONNECTED) {
            Log.e(TAG, "CONNECTED!!! :D");
            ready = true;
        }else
            Log.i(TAG, "BTAudioIsReady: " + Boolean.toString(ready));

        return ready;
    }

    //Returns true if a connected device on the connected device list is on the BAPMPreferences.
    //getBTDevices List that is set by the user in the UI
    public static boolean isDeviceOnBAPMList(Context context){
        Set<String> userBTDeviceList = BAPMPreferences.getBTDevices(context);
        List<String> connectedBTDeviceList = VariousLists.ConnectedBTDevices;

        if(VariousLists.ConnectedBTDevices != null){
            return !Collections.disjoint(userBTDeviceList, connectedBTDeviceList);
        }else{
            Log.i(TAG, "ConnectedBTDevices List = null");
        }
        return false;
    }

    //Creates notification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    public static void BTConnectPhoneDoStuff(Context context, AudioManager am){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(context);
        boolean playMusic = BAPMPreferences.getAutoPlayMusic(context);

        RingerControl ringerControl = new RingerControl(am);

        Notification.BAPMMessage(context);

        if(screenON){
            screenONLock.enableWakeLock(context);
        }

        if(priorityMode){
            currentRingerSet = ringerControl.ringerSetting();
            ringerControl.soundsOFF();
        }

        if(unlockScreen){
            launchMainActivity(context);
        }

        if(launchMusicPlayer) {
            try {
                LaunchApp.musicPlayerLaunch(context, 2, launchMaps);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }else{
            Log.i(TAG, "Launch Music Player is OFF");
        }

        if(playMusic){
            PlayMusic.auto_Play(context, am);
        }

        if(launchMaps && !launchMusicPlayer){
            LaunchApp.delayLaunchMaps(context, 2);
        }

        if(volumeMAX){
            VolumeControl volumeControl = new VolumeControl(context, am);
            volumeControl.checkSetMAXVol(12, 4);
        }

        if(playMusic){
            PlayMusic.checkIfPlaying(context, am);
        }
        VariableStore.setRanBTConnectPhoneDoStuff(true);
    }

    //Removes notification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music and abandons AudioFocus
    public static void BTDisconnectPhoneDoStuff(Context context, AudioManager am, AudioFocus af){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        boolean sendToBackground = BAPMPreferences.getSendToBackground(context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);

        RingerControl ringerControl = new RingerControl(am);

        Notification.removeBAPMMessage(context);

        if(screenON){
            screenONLock.releaseWakeLock();
        }

        if(priorityMode){
            try {
                switch(currentRingerSet){
                    case AudioManager.RINGER_MODE_SILENT:
                        Log.i(TAG, "Phone is on Silent");
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        ringerControl.vibrateOnly();
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        ringerControl.soundsON();
                        break;
                }
            }catch(Exception e){
                Log.e(TAG, e.getMessage());
            }
        }

        if(launchMusicPlayer) {
            PlayMusic.pause(am);
        }

        if(volumeMAX){
            VolumeControl volumeControl = new VolumeControl(context, am);
            volumeControl.setOriginalVolume();
        }

        if(sendToBackground) {
            sendEverythingToBackground(context);
        }

        try {
            af.abandonAudioFocus();
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        VariableStore.setRanBTConnectPhoneDoStuff(false);
    }

    //Launch MainActivity, used for unlocking the screen
    private static void launchMainActivity(Context context){
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private static void sendEverythingToBackground(Context context){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
