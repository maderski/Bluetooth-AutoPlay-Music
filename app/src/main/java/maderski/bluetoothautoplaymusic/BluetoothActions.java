package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Jason on 2/22/16.
 */
public class BluetoothActions {

    private static final String TAG = BluetoothActions.class.getName();

    private static boolean ranActionsOnBTConnect;
    private static int currentRingerSet;

    private ScreenONLock screenONLock;
    private Context context;
    private AudioManager audioManager;
    private Notification notification;
    private VolumeControl volumeControl;

    public BluetoothActions(Context context, AudioManager audioManager, ScreenONLock screenONLock, Notification notification, VolumeControl volumeControl){
        this.context = context;
        this.audioManager = audioManager;
        this.screenONLock = screenONLock;
        this.notification = notification;
        this.volumeControl = volumeControl;
    }

    //Return true if Bluetooth Audio is ready
    public boolean isBTAudioIsReady(Intent intent){
        boolean ready = false;
        int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
        if(state == BluetoothA2dp.STATE_CONNECTED) {
            if(BuildConfig.DEBUG)
                Log.e(TAG, "CONNECTED!!! :D");
            ready = true;
        }else {
            if (BuildConfig.DEBUG)
                Log.i(TAG, "BTAudioIsReady: " + Boolean.toString(ready));
        }

        return ready;
    }

    public void OnBTConnect(){
        boolean waitTillOffPhone = BAPMPreferences.getWaitTillOffPhone(context);

        if(waitTillOffPhone){
            Telephone telephone = new Telephone(context);
            if(Power.isPluggedIn(context)){
                if(telephone.isOnCall()) {
                    if(BuildConfig.DEBUG)
                        Log.i(TAG, "ON a call");
                    //Run CheckIfOnPhone
                    telephone.CheckIfOnPhone(audioManager, volumeControl);
                }else{
                    if(BuildConfig.DEBUG)
                        Log.i(TAG, "NOT on a call");
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
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(context);
        boolean playMusic = BAPMPreferences.getAutoPlayMusic(context);

        RingerControl ringerControl = new RingerControl(audioManager);
        LaunchApp launchApp = new LaunchApp(context);

        notification.BAPMMessage(context);

        if(screenON){
            screenONLock.enableWakeLock(context);
        }

        if(priorityMode){
            currentRingerSet = ringerControl.ringerSetting();
            ringerControl.soundsOFF();
        }

        if(unlockScreen){
            launchApp.launchBAPMActivity();
        }

        if(volumeMAX){
            volumeControl.checkSetMAXVol(12,4);
        }

        if(launchMusicPlayer) {
            try {
                launchApp.musicPlayerLaunch(2, launchMaps);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }else{
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Launch Music Player is OFF");
        }

        if(playMusic){
            PlayMusic music = new PlayMusic(context, audioManager);
            music.auto_Play();
        }

        if(launchMaps && !launchMusicPlayer){
            launchApp.launchMaps(2);
        }

        ranActionsOnBTConnect = true;
    }

    //Removes notification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    public void actionsOnBTDisconnect(){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        boolean sendToBackground = BAPMPreferences.getSendToBackground(context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);

        RingerControl ringerControl = new RingerControl(audioManager);
        LaunchApp launchApp = new LaunchApp(context);
        notification.removeBAPMMessage(context);

        if(screenON){
            screenONLock.releaseWakeLock();
        }

        if(priorityMode){
            try {
                switch(currentRingerSet){
                    case AudioManager.RINGER_MODE_SILENT:
                        if(BuildConfig.DEBUG)
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
            PlayMusic music = new PlayMusic(context, audioManager);
            music.pause();
        }

        if(volumeMAX){
            volumeControl.setOriginalVolume();
        }

        if(sendToBackground) {
            launchApp.sendEverythingToBackground();
        }

        ranActionsOnBTConnect = false;
    }

    public static boolean getRanActionsOnBTConnect(){
        return ranActionsOnBTConnect;
    }
    public static void setRanActionsOnBTConnect(boolean didItRun){
        ranActionsOnBTConnect = didItRun;
    }
}
