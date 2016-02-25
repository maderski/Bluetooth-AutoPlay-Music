package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 2/22/16.
 */
public class BluetoothActions {

    final static String TAG = BluetoothActions.class.getName();

    private static ScreenONLock screenONLock = new ScreenONLock();

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

    //Creates notification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    public static void BTConnectPhoneDoStuff(Context context, String btDevice){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);

        VariableStore.ranBluetoothDoStuff = true;
        VariableStore.ringerControl = new RingerControl(context);
        Notification.BAPMMessage(context, btDevice);

        if(screenON){
            screenONLock.enableWakeLock(context);
        }

        if(priorityMode){
            VariableStore.currentRingerSet = VariableStore.ringerControl.ringerSetting();
            VariableStore.ringerControl.soundsOFF();
        }

        if(volumeMAX){
            VariableStore.ringerControl.volumeMAX();
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
    public static void BTDisconnectPhoneDoStuff(Context context){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);

        Notification.removeBAPMMessage(context);

        if(screenON){
            screenONLock.releaseWakeLock(context);
        }

        if(priorityMode){
            try {
                switch(VariableStore.currentRingerSet){
                    case AudioManager.RINGER_MODE_SILENT:
                        Log.i(TAG, "Phone is on Silent");
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        VariableStore.ringerControl.vibrateOnly();
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        VariableStore.ringerControl.soundsON();
                        break;
                }
            }catch(Exception e){
                Log.e(TAG, e.getMessage());
            }
        }

        if(launchMusicPlayer) {
            try {
                PlayMusic.pause();
                AudioFocus.abandonAudioFocus();
            }catch(Exception e){
                Log.e(TAG, e.getMessage());
            }
        }

        VariableStore.ranBluetoothDoStuff = false;
        VariableStore.isBTConnected = false;
    }

    //Launch MainActivity, used for unlocking the screen
    private static void launchMainActivity(Context context){
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
