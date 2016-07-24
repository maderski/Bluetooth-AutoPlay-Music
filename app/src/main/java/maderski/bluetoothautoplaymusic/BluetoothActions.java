package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 2/22/16.
 */
public class BluetoothActions {

    final static String TAG = BluetoothActions.class.getName();

    private static boolean ranActionsOnBTConnect;
    private static ScreenONLock screenONLock = new ScreenONLock();
    private static int currentRingerSet;

    private Context _context;
    private AudioManager _audioManager;

    public BluetoothActions(Context context, AudioManager audioManager){
        _context = context;
        _audioManager = audioManager;
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

    //Returns true if a connected device on the connected device list is on the BAPMPreferences.
    //getBTDevices List that is set by the user in the UI
    public boolean isDeviceOnBAPMList(Context context){
        Set<String> userBTDeviceList = BAPMPreferences.getBTDevices(context);
        List<String> connectedBTDeviceList = VariousLists.ConnectedBTDevices;

        if(VariousLists.ConnectedBTDevices != null){
            return !Collections.disjoint(userBTDeviceList, connectedBTDeviceList);
        }else{
            if(BuildConfig.DEBUG)
                Log.i(TAG, "ConnectedBTDevices List = null");
        }
        return false;
    }

    public void OnBTConnect(){
        boolean waitTillOffPhone = BAPMPreferences.getWaitTillOffPhone(_context);

        if(waitTillOffPhone){
            Telephone telephone = new Telephone(_context);
            if(Power.isPluggedIn(_context)){
                if(telephone.isOnCall()) {
                    if(BuildConfig.DEBUG)
                        Log.i(TAG, "ON a call");
                    //Run CheckIfOnPhone
                    CheckIfOnPhone(_context);
                }else{
                    if(BuildConfig.DEBUG)
                        Log.i(TAG, "NOT on a call");
                    actionsOnBTConnect();
                }
            }else{
                if(telephone.isOnCall()) {
                    Notification.launchBAPM(_context);
                }else{
                    actionsOnBTConnect();
                }
            }
        }else{
            actionsOnBTConnect();
        }
    }

    private void CheckIfOnPhone(Context context){
        final Context ctx = context;
        final Telephone telephone = new Telephone(ctx);

        int _seconds = 7200000; //check for 2 hours
        int _interval = 6000; //6 second interval

        new CountDownTimer(_seconds, _interval)
        {
            public void onTick(long millisUntilFinished) {
                if(Power.isPluggedIn(ctx)){
                    if(telephone.isOnCall()){
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "On Call, check again in 6 sec");
                    }else{
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "Off Call, Launching Bluetooth Autoplay music");
                        cancel();
                        //Get Original Volume and Launch Bluetooth Autoplay Music
                        delayGetOrigVol();
                    }
                }else{
                    //Bailing cause phone is not plugged in
                    if(BuildConfig.DEBUG)
                        Log.i(TAG, "Phone is no longer plugged in to power");
                    cancel();
                }
            }

            public void onFinish() {
                //does nothing currently
            }
        }.start();
    }

    //Wait 3 seconds before getting the Original Volume and return true when done
    public void delayGetOrigVol(){
        final Context ctx = _context;
        final AudioManager am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
        if(am.isBluetoothA2dpOn()) {
            new CountDownTimer(6000,
                    1000) {
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished > 3000 && millisUntilFinished < 4000) {
                        //Get original volume
                        VolumeControl.originalMediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "Original Media Volume is: " + Integer.toString(VolumeControl.originalMediaVolume));
                    }
                }

                public void onFinish() {
                    actionsOnBTConnect();
                }
            }.start();
        }
    }

    //Creates notification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    public void actionsOnBTConnect(){
        boolean screenON = BAPMPreferences.getKeepScreenON(_context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(_context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(_context);
        boolean unlockScreen = BAPMPreferences.getUnlockScreen(_context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(_context);
        boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(_context);
        boolean playMusic = BAPMPreferences.getAutoPlayMusic(_context);

        RingerControl ringerControl = new RingerControl(_audioManager);

        Notification.BAPMMessage(_context);

        if(screenON){
            screenONLock.enableWakeLock(_context);
        }

        if(priorityMode){
            currentRingerSet = ringerControl.ringerSetting();
            ringerControl.soundsOFF();
        }

        if(unlockScreen){
            //launchMainActivity(_context);
            launchBAPMActivity(_context);
        }

        if(volumeMAX){
            VolumeControl volumeControl = new VolumeControl(_audioManager);
            volumeControl.checkSetMAXVol(12,4);
        }

        if(launchMusicPlayer) {
            try {
                LaunchApp.musicPlayerLaunch(_context, 2, launchMaps);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }else{
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Launch Music Player is OFF");
        }

        if(playMusic){
            PlayMusic music = new PlayMusic(_context, _audioManager);
            music.auto_Play();
        }

        if(launchMaps && !launchMusicPlayer){
            LaunchApp.delayLaunchMaps(_context, 2);
        }

        ranActionsOnBTConnect = true;
    }

    //Removes notification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    public void actionsOnBTDisconnect(){
        boolean screenON = BAPMPreferences.getKeepScreenON(_context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(_context);
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(_context);
        boolean sendToBackground = BAPMPreferences.getSendToBackground(_context);
        boolean volumeMAX = BAPMPreferences.getMaxVolume(_context);

        RingerControl ringerControl = new RingerControl(_audioManager);

        Notification.removeBAPMMessage(_context);

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
            PlayMusic music = new PlayMusic(_context, _audioManager);
            music.pause();
        }

        if(volumeMAX){
            VolumeControl volumeControl = new VolumeControl(_audioManager);
            volumeControl.setOriginalVolume();
        }

        if(sendToBackground) {
            sendEverythingToBackground(_context);
        }

        ranActionsOnBTConnect = false;
    }

    //Launch MainActivity, used for unlocking the screen
    private void launchMainActivity(Context context){
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void sendEverythingToBackground(Context context){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void launchBAPMActivity(Context context){
        Intent i = new Intent(context, LaunchBAPMActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static boolean getRanActionsOnBTConnect(){
        return ranActionsOnBTConnect;
    }
    public static void setRanActionsOnBTConnect(boolean didItRun){
        ranActionsOnBTConnect = didItRun;
    }
}
