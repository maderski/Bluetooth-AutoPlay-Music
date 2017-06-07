package maderski.bluetoothautoplaymusic.BluetoothActions;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.Controls.RingerControl;
import maderski.bluetoothautoplaymusic.Controls.VolumeControl;
import maderski.bluetoothautoplaymusic.Controls.WakeLockControl.ScreenONLock;
import maderski.bluetoothautoplaymusic.Controls.WifiControl;
import maderski.bluetoothautoplaymusic.Helpers.PermissionHelper;
import maderski.bluetoothautoplaymusic.Helpers.PowerHelper;
import maderski.bluetoothautoplaymusic.Helpers.TimeHelper;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.Notification;
import maderski.bluetoothautoplaymusic.Receivers.CustomReceiver;
import maderski.bluetoothautoplaymusic.Receivers.NotifPolicyAccessChangedReceiver;
import maderski.bluetoothautoplaymusic.Receivers.PowerReceiver;
import maderski.bluetoothautoplaymusic.Services.BTStateChangedService;
import maderski.bluetoothautoplaymusic.Services.OnBTConnectService;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.Telephone;
import maderski.bluetoothautoplaymusic.Utils.ReceiverUtils;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 6/3/17.
 */

public class BTConnectActions {
    private static final String TAG = "BTConnectActions";

    private final ScreenONLock mScreenONLock;
    private final Context context;
    private final Notification mNotification;
    private final VolumeControl mVolumeControl;
    private final PlayMusicControl mPlayMusicControl;

    public BTConnectActions(Context context){
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
        final LaunchApp launchApp = new LaunchApp();
        final boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);

        showBTAMNotification();
        setVolumeToMax();
        turnTheScreenOn();

        if(unlockScreen){
            performActionsDelay(launchApp, unlockScreen);
        } else {
            launchMusicMapApp(launchApp);
            autoPlayMusic(6);
            setWifiOff(launchApp);
            putPhoneInDoNotDisturb();
        }

        BAPMDataPreferences.setRanActionsOnBtConnect(context, true);
        ServiceUtils.stopService(context, OnBTConnectService.class, OnBTConnectService.TAG);
    }

    private void performActionsDelay(final LaunchApp launchApp, final boolean unlockScreen){
        int seconds = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ? 30000 : 8000;

        new CountDownTimer(seconds, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 &&
                        !((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isDeviceLocked()) {
                    cancel();
                    onFinish();
                }
                Log.d(TAG, "LOCKED mills left to check: " + String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                unlockTheScreen(launchApp, unlockScreen);
                launchMusicMapApp(launchApp);
                autoPlayMusic(6);
                setWifiOff(launchApp);
                putPhoneInDoNotDisturb();
            }
        }.start();
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

    private void unlockTheScreen(LaunchApp launchApp, boolean unlockScreen){
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

    private void autoPlayMusic(final int checkToPlaySeconds){
        boolean playMusic = BAPMPreferences.getAutoPlayMusic(context);
        if (playMusic) {
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

    private void putPhoneInDoNotDisturb(){
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
}
