package maderski.bluetoothautoplaymusic;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.Controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.Controls.RingerControl;
import maderski.bluetoothautoplaymusic.Controls.VolumeControl;
import maderski.bluetoothautoplaymusic.Controls.WakeLockControl.ScreenONLock;
import maderski.bluetoothautoplaymusic.Controls.WifiControl;
import maderski.bluetoothautoplaymusic.Helpers.PermissionHelper;
import maderski.bluetoothautoplaymusic.Helpers.PowerHelper;
import maderski.bluetoothautoplaymusic.Helpers.ReceiverHelper;
import maderski.bluetoothautoplaymusic.Helpers.TimeHelper;
import maderski.bluetoothautoplaymusic.Receivers.BTStateChangedReceiver;
import maderski.bluetoothautoplaymusic.Receivers.NotifPolicyAccessChangedReceiver;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 2/22/16.
 */
public class BluetoothActions {

    private static final String TAG = BluetoothActions.class.getName();

    private ScreenONLock mScreenONLock;
    private Context context;
    private Notification mNotification;
    private VolumeControl mVolumeControl;
    private PlayMusicControl mPlayMusicControl;

    public BluetoothActions(Context context){
        this.context = context;
        this.mScreenONLock = ScreenONLock.getInstance();
        this.mNotification = new Notification();
        this.mVolumeControl = new VolumeControl(context);
        this.mPlayMusicControl = new PlayMusicControl(context);
    }

    public void OnBTConnect(){
        boolean waitTillOffPhone = BAPMPreferences.getWaitTillOffPhone(context);

        if(waitTillOffPhone){
            Telephone telephone = new Telephone(context);
            if(PowerHelper.isPluggedIn(context)){
                if(telephone.isOnCall()) {
                    Log.d(TAG, "ON a call");
                    //Run CheckIfOnPhone
                    telephone.CheckIfOnPhone(mVolumeControl);
                }else{
                    Log.d(TAG, "NOT on a call");
                    actionsOnBTConnect();
                }
            }else{
                if(telephone.isOnCall()) {
                    mNotification.launchBAPM(context);
                }else{
                    actionsOnBTConnect();
                }
            }
        }else{
            actionsOnBTConnect();
        }
    }

    //Creates mNotification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    public void actionsOnBTConnect(){
        synchronized (this) {
            LaunchApp launchApp = new LaunchApp();

            showBTAMNotification();
            turnTheScreenOn();
            unlockTheScreen(launchApp);
            setVolumeToMax();
            autoPlayMusic(7);
            launchMusicMapApp(launchApp);
            setWifiOff(launchApp);
            putPhoneInDoNotDistrub();

            BAPMDataPreferences.setRanActionsOnBtConnect(context, true);
        }
    }

    private void showBTAMNotification(){
        String mapChoice = BAPMPreferences.getMapsChoice(context);
        boolean canShowNotification = BAPMPreferences.getShowNotification(context);
        if (canShowNotification) {
            mNotification.BAPMMessage(context, mapChoice);
        }
    }

    private void turnTheScreenOn(){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        if (screenON) {
            //Try to releaseWakeLock() in case for some reason it was not released on disconnect
            if (mScreenONLock.wakeLockHeld()) {
                mScreenONLock.releaseWakeLock();
            }
            mScreenONLock.enableWakeLock(context);
        }
    }

    private void unlockTheScreen(LaunchApp launchApp){
        boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);
        if (unlockScreen) {
            boolean isKeyguardLocked = ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardLocked();
            Log.d(TAG, "Is keyguard locked: " + Boolean.toString(isKeyguardLocked));
            if (isKeyguardLocked) {
                launchApp.launchBAPMActivity(context);
            }
        }
    }
    private void setVolumeToMax(){
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        if (volumeMAX) {
//            mVolumeControl.saveOriginalVolume();
//            Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mVolumeControl.checkSetMAXVol(4);
                }
            };
            handler.postDelayed(runnable, 3000);
        }
    }

    private void autoPlayMusic(int checkToPlaySeconds){
        boolean playMusic = BAPMPreferences.getAutoPlayMusic(context);
        if (playMusic) {
            mPlayMusicControl.play();
            mPlayMusicControl.checkIfPlaying(context, checkToPlaySeconds);
        }
    }

    private void launchMusicMapApp(LaunchApp launchApp){
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(context);
        boolean mapsCanLaunch = launchApp.canMapsLaunchDuringThisTime(context)
                && launchApp.canMapsLaunchOnThisDay(context);

        if (launchMusicPlayer && !launchMaps || launchMusicPlayer && !mapsCanLaunch) {
            launchApp.musicPlayerLaunch(context, 3);
        }

        if (launchMaps) {
            launchApp.launchMaps(context, 3);
        }
    }

    private void setWifiOff(LaunchApp launchApp){
        boolean isWifiOffDevice = BAPMDataPreferences.getIsTurnOffWifiDevice(context);
        if (isWifiOffDevice) {
            int morningStartTime = BAPMPreferences.getMorningStartTime(context);
            int morningEndTime = BAPMPreferences.getMorningEndTime(context);

            int eveningStartTime = BAPMPreferences.getEveningStartTime(context);
            int eveningEndTime = BAPMPreferences.getEveningEndTime(context);

            TimeHelper timeHelper = new TimeHelper(morningStartTime, morningEndTime, eveningStartTime, eveningEndTime);
            boolean isWorkLocation = timeHelper.getDirectionLocation().equals(LaunchApp.DirectionLocations.WORK);

            boolean canChangeWifiState = !BAPMPreferences.getWifiUseMapTimeSpans(context)
                    || (isWorkLocation && launchApp.canMapsLaunchOnThisDay(context));
            if (canChangeWifiState && WifiControl.isWifiON(context)) {
                WifiControl.wifiON(context, false);
            }
        }
    }

    private void putPhoneInDoNotDistrub(){
        RingerControl ringerControl = new RingerControl(context);
        boolean priorityMode = BAPMPreferences.getPriorityMode(context);

        if (priorityMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                boolean hasDoNotDisturbPerm = PermissionHelper.checkDoNotDisturbPermission(context, 10);
                if (hasDoNotDisturbPerm) {
                    BAPMDataPreferences.setCurrentRingerSet(context, ringerControl.ringerSetting());
                    ringerControl.soundsOFF();
                } else {
                    BroadcastReceiver broadcastReceiver = new NotifPolicyAccessChangedReceiver();
                    IntentFilter intentFilter = new IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED);
                    context.getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
                }
            } else {
                BAPMDataPreferences.setCurrentRingerSet(context, ringerControl.ringerSetting());
                ringerControl.soundsOFF();
            }
        }
    }

    //Removes mNotification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    public void actionsOnBTDisconnect(){
        synchronized (this) {
            LaunchApp launchApp = new LaunchApp();
            RingerControl ringerControl = new RingerControl(context);

            removeBAPMNotification();
            pauseMusic();
            turnOffPriorityMode(ringerControl);
            sendAppToBackground(launchApp);
            closeWaze(launchApp);
            setWifiOn(launchApp);
            stopKeepingScreenOn();
            setVolumeBack(ringerControl);

            BAPMDataPreferences.setRanActionsOnBtConnect(context, false);
        }
    }

    private void removeBAPMNotification(){
        boolean canShowNotification = BAPMPreferences.getShowNotification(context);

        if(canShowNotification) {
            mNotification.removeBAPMMessage(context);
        }
    }

    private void pauseMusic(){
        boolean playMusic = BAPMPreferences.getAutoPlayMusic(context);
        if (playMusic) {
            mPlayMusicControl.pause();
        }
    }

    private void sendAppToBackground(LaunchApp launchApp){
        boolean sendToBackground = BAPMPreferences.getSendToBackground(context);
        if (sendToBackground) {
            launchApp.sendEverythingToBackground(context);
        }
    }

    private void turnOffPriorityMode(RingerControl ringerControl){

        boolean priorityMode = BAPMPreferences.getPriorityMode(context);
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
    }

    private void closeWaze(LaunchApp launchApp){
        boolean closeWaze = BAPMPreferences.getCloseWazeOnDisconnect(context)
                && launchApp.checkPkgOnPhone(context, PackageTools.PackageName.WAZE)
                && BAPMPreferences.getMapsChoice(context).equals(PackageTools.PackageName.WAZE);
        if(closeWaze) {
            launchApp.closeWazeOnDisconnect(context);
        }
    }

    private void setWifiOn(LaunchApp launchApp){
        boolean isWifiOffDevice = BAPMDataPreferences.getIsTurnOffWifiDevice(context);
        if(isWifiOffDevice){
            int morningStartTime = BAPMPreferences.getMorningStartTime(context);
            int morningEndTime = BAPMPreferences.getMorningEndTime(context);

            int eveningStartTime = BAPMPreferences.getEveningStartTime(context);
            int eveningEndTime = BAPMPreferences.getEveningEndTime(context);

            TimeHelper timeHelper = new TimeHelper(morningStartTime, morningEndTime, eveningStartTime, eveningEndTime);
            boolean isHomeLocation = timeHelper.getDirectionLocation().equals(LaunchApp.DirectionLocations.HOME);

            boolean canChangeWifiState = !BAPMPreferences.getWifiUseMapTimeSpans(context)
                    || (isHomeLocation && launchApp.canMapsLaunchOnThisDay(context));
            if(canChangeWifiState && !WifiControl.isWifiON(context)) {
                WifiControl.wifiON(context, true);
            }
            BAPMDataPreferences.setIsTurnOffWifiDevice(context, false);
        }
    }

    private void stopKeepingScreenOn(){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        if (screenON) {
            mScreenONLock.releaseWakeLock();
        }
    }

    private void setVolumeBack(RingerControl ringerControl){
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        if (volumeMAX) {
            mVolumeControl.setToOriginalVolume(ringerControl);
        }
    }

    public void actionsBTStateOff(){
        // Pause music
        PlayMusicControl playMusicControl = new PlayMusicControl(context);
        playMusicControl.pause();

        // Put music volume back to original volume
        mVolumeControl.setToOriginalVolume(new RingerControl(context));

        if(BuildConfig.DEBUG)
            Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show();

        if(BAPMDataPreferences.getRanActionsOnBtConnect(context)) {
            actionsOnBTDisconnect();
        }
        ReceiverHelper.stopReceiver(context, BTStateChangedReceiver.class);
    }
}
