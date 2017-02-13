package maderski.bluetoothautoplaymusic;

import android.app.NotificationManager;
import android.bluetooth.BluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.Receivers.NotifPolicyAccessChangedReceiver;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 2/22/16.
 */
public class BluetoothActions {

    private static final String TAG = BluetoothActions.class.getName();

    private ScreenONLock screenONLock;
    private Context context;
    private Notification notification;
    private VolumeControl volumeControl;

    public BluetoothActions(Context context){
        this.context = context;
        this.screenONLock = ScreenONLock.getInstance();
        this.notification = new Notification();
        this.volumeControl = new VolumeControl(context);
    }

    //Return true if Bluetooth Audio is ready
    public boolean isBTAudioIsReady(Intent intent){
        boolean ready = false;
        int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
        if(state == BluetoothA2dp.STATE_CONNECTED) {
            Log.d(TAG, "CONNECTED!!! :D");
            ready = true;
        }else {
            Log.d(TAG, "BTAudioIsReady: " + Boolean.toString(ready));
        }

        return ready;
    }

    public void OnBTConnect(){
        boolean waitTillOffPhone = BAPMPreferences.getWaitTillOffPhone(context);

        if(waitTillOffPhone){
            Telephone telephone = new Telephone(context);
            if(Power.isPluggedIn(context)){
                if(telephone.isOnCall()) {
                    Log.d(TAG, "ON a call");
                    //Run CheckIfOnPhone
                    telephone.CheckIfOnPhone(volumeControl);
                }else{
                    Log.d(TAG, "NOT on a call");
                    actionsOnBTConnect();
                }
            }else{
                if(telephone.isOnCall()) {
                    notification.launchBAPM(context);
                }else{
                    actionsOnBTConnect();
                }
            }
        }else{
            actionsOnBTConnect();
        }
    }

    //Creates notification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    public void actionsOnBTConnect(){
        synchronized (this) {
            boolean screenON = BAPMPreferences.getKeepScreenON(context);
            boolean priorityMode = BAPMPreferences.getPriorityMode(context);
            boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
            boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);
            boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
            boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(context);
            boolean playMusic = BAPMPreferences.getAutoPlayMusic(context);

            String mapChoice = BAPMPreferences.getMapsChoice(context);

            RingerControl ringerControl = new RingerControl(context);
            LaunchApp launchApp = new LaunchApp();

            notification.BAPMMessage(context, mapChoice);

            if (screenON) {
                //Try to releaseWakeLock() in case for some reason it was not released on disconnect
                if (screenONLock.wakeLockHeld()) {
                    screenONLock.releaseWakeLock();
                }
                screenONLock.enableWakeLock(context);
            }

            if (unlockScreen) {
                launchApp.launchBAPMActivity(context);
            }

            if (volumeMAX) {
                volumeControl.checkSetMAXVol(context, 4);
            }

            if (launchMusicPlayer && !launchMaps) {
                try {
                    launchApp.musicPlayerLaunch(context, 3);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            if (playMusic) {
                PlayMusic music = new PlayMusic(context);
                music.play();
                music.checkIfPlaying(5);
            }

            if (launchMaps) {
                launchApp.launchMaps(context, 3);
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Permissions permissions = new Permissions();
                boolean hasDoNotDisturbPerm = permissions.checkDoNotDisturbPermission(context, 10);
                if (priorityMode && hasDoNotDisturbPerm) {
                    BAPMDataPreferences.setCurrentRingerSet(context, ringerControl.ringerSetting());
                    ringerControl.soundsOFF();
                } else {
                    BroadcastReceiver broadcastReceiver = new NotifPolicyAccessChangedReceiver();
                    IntentFilter intentFilter = new IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED);
                    context.getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
                }
            } else {
                if (priorityMode) {
                    BAPMDataPreferences.setCurrentRingerSet(context, ringerControl.ringerSetting());
                    ringerControl.soundsOFF();
                }
            }

            BAPMDataPreferences.setRanActionsOnBtConnect(context, true);
        }
    }

    //Removes notification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    public void actionsOnBTDisconnect(){
        synchronized (this) {
            boolean screenON = BAPMPreferences.getKeepScreenON(context);
            boolean priorityMode = BAPMPreferences.getPriorityMode(context);
            boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
            boolean sendToBackground = BAPMPreferences.getSendToBackground(context);
            boolean volumeMAX = BAPMPreferences.getMaxVolume(context);

            RingerControl ringerControl = new RingerControl(context);
            LaunchApp launchApp = new LaunchApp();

            notification.removeBAPMMessage(context);

            if (screenON) {
                screenONLock.releaseWakeLock();
            }

            if (priorityMode) {
                int currentRinger = BAPMDataPreferences.getCurrentRingerSet(context);
                try {
                    switch (currentRinger) {
                        case AudioManager.RINGER_MODE_SILENT:
                            Log.d(TAG, "Phone is on Silent");
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            ringerControl.vibrateOnly();
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            ringerControl.soundsON();
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            if (launchMusicPlayer) {
                PlayMusic playMusic = new PlayMusic(context);
                playMusic.pause();
            }

            if (volumeMAX) {
                volumeControl.setOriginalVolume(context);
            }

            if (sendToBackground) {
                launchApp.sendEverythingToBackground(context);
            }

            BAPMDataPreferences.setRanActionsOnBtConnect(context, false);
        }
    }

    public void actionsBTStateOff(){
        // Pause music
        PlayMusic playMusic = new PlayMusic(context);
        playMusic.pause();

        // Put music volume back to original volume
        volumeControl.setOriginalVolume(context);

        if(BuildConfig.DEBUG)
            Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show();

        actionsOnBTDisconnect();
    }
}
