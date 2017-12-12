package maderski.bluetoothautoplaymusic.BluetoothActions;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.Controls.RingerControl;
import maderski.bluetoothautoplaymusic.Controls.VolumeControl;
import maderski.bluetoothautoplaymusic.Controls.WifiControl;
import maderski.bluetoothautoplaymusic.Helpers.PermissionHelper;
import maderski.bluetoothautoplaymusic.Helpers.PowerHelper;
import maderski.bluetoothautoplaymusic.Helpers.TimeHelper;
import maderski.bluetoothautoplaymusic.Helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.Notification;
import maderski.bluetoothautoplaymusic.Receivers.NotifPolicyAccessChangedReceiver;
import maderski.bluetoothautoplaymusic.Services.OnBTConnectService;
import maderski.bluetoothautoplaymusic.Services.WakeLockService;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.Telephone;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 6/3/17.
 */

public class BTConnectActions {
    private static final String TAG = "BTConnectActions";

    private final Context context;
    private final Notification mNotification;
    private final VolumeControl mVolumeControl;
    private final PlayMusicControl mPlayMusicControl;
    private final LaunchAppHelper mLaunchAppHelper;

    public BTConnectActions(Context context){
        this.context = context;
        mNotification = new Notification();
        mVolumeControl = new VolumeControl(context);
        mPlayMusicControl = new PlayMusicControl(context);
        mLaunchAppHelper = new LaunchAppHelper();
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
        final boolean unlockScreen = BAPMPreferences.getUnlockScreen(context);

        showBTAMNotification();
        setVolumeToMax();
        turnTheScreenOn();

        if(unlockScreen){
            performActionsDelay();
        } else {
            launchMusicMapApp();
            autoPlayMusic(6);
            setWifiOff();
            putPhoneInDoNotDisturb();
        }

        BAPMDataPreferences.setRanActionsOnBtConnect(context, true);
        ServiceUtils.stopService(context, OnBTConnectService.class, OnBTConnectService.TAG);
    }

    private void performActionsDelay(){
        int seconds = 30000;

        new CountDownTimer(seconds, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                boolean isDeviceLocked = keyguardManager.isDeviceLocked();
                Log.d(TAG, "IS DEVICE LOCKED: " + String.valueOf(isDeviceLocked));
                if (!isDeviceLocked && millisUntilFinished < 28000) {
                    cancel();
                    onFinish();
                }
                Log.d(TAG, "LOCKED mills left to check: " + String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                unlockTheScreen();
                launchMusicMapApp();
                autoPlayMusic(6);
                setWifiOff();
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
            ServiceUtils.startService(context, WakeLockService.class, WakeLockService.TAG);
        }
    }

    private void unlockTheScreen(){
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isKeyguardLocked = keyguardManager.isKeyguardLocked();
        Log.d(TAG, "Is keyguard locked: " + Boolean.toString(isKeyguardLocked));
        if (isKeyguardLocked) {
            mLaunchAppHelper.launchBAPMActivity(context);
        }
    }

    private void setVolumeToMax(){
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        if (volumeMAX) {
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

    private void launchMusicMapApp(){
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(context);
        boolean mapsCanLaunch = mLaunchAppHelper.canMapsLaunchNow(context);

        if (launchMusicPlayer && !launchMaps || launchMusicPlayer && !mapsCanLaunch) {
            mLaunchAppHelper.musicPlayerLaunch(context, 3);
        }

        if (launchMaps) {
            mLaunchAppHelper.launchMaps(context, 3);
        }
    }

    private void setWifiOff(){
        boolean isWifiOffDevice = BAPMDataPreferences.getIsTurnOffWifiDevice(context);
        if (isWifiOffDevice) {
            int morningStartTime = BAPMPreferences.getMorningStartTime(context);
            int morningEndTime = BAPMPreferences.getMorningEndTime(context);

            int current24hrTime = TimeHelper.getCurrent24hrTime();

            TimeHelper timeHelperMorning = new TimeHelper(morningStartTime, morningEndTime, current24hrTime);
            boolean canLaunch = timeHelperMorning.isWithinTimeSpan();
            String directionLocation = canLaunch ? LaunchAppHelper.DirectionLocations.WORK : LaunchAppHelper.DirectionLocations.HOME;

            boolean canChangeWifiState = !BAPMPreferences.getWifiUseMapTimeSpans(context)
                    || (canLaunch && mLaunchAppHelper.canLaunchOnThisDay(context, directionLocation));
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
