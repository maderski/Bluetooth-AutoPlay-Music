package maderski.bluetoothautoplaymusic.bluetoothactions;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.controls.RingerControl;
import maderski.bluetoothautoplaymusic.controls.VolumeControl;
import maderski.bluetoothautoplaymusic.controls.WifiControl;
import maderski.bluetoothautoplaymusic.helpers.TimeHelper;
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.BAPMNotification;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.services.BTDisconnectService;
import maderski.bluetoothautoplaymusic.services.WakeLockService;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

/**
 * Created by Jason on 6/3/17.
 */

public class BTDisconnectActions {
    private static final String TAG = "BTDisconnectActions";

    private final Context context;
    private final BAPMNotification mBAPMNotification;
    private final VolumeControl mVolumeControl;
    private final PlayMusicControl mPlayMusicControl;

    public BTDisconnectActions(Context context){
        this.context = context;
        this.mBAPMNotification = new BAPMNotification();
        this.mVolumeControl = new VolumeControl(context);
        this.mPlayMusicControl = new PlayMusicControl(context);
    }

    //Removes mNotification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    public void actionsOnBTDisconnect(){
        LaunchAppHelper launchAppHelper = new LaunchAppHelper();
        RingerControl ringerControl = new RingerControl(context);

        removeBAPMNotification();
        pauseMusic();
        turnOffPriorityMode(ringerControl);
        sendAppToBackground(launchAppHelper);
        closeWaze(launchAppHelper);
        setWifiOn(launchAppHelper);
        stopKeepingScreenOn();

        setVolumeBack(ringerControl);

        stopService();
    }

    private void stopService() {
        BAPMDataPreferences.INSTANCE.setRanActionsOnBtConnect(context, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ServiceUtils.INSTANCE.stopService(context, BTDisconnectService.class, BTDisconnectService.TAG);
            }
        }, 5000);
    }

    private void removeBAPMNotification(){
        boolean canShowNotification = BAPMPreferences.INSTANCE.getShowNotification(context);

        if(canShowNotification) {
            mBAPMNotification.removeBAPMMessage(context);
        }
    }

    private void pauseMusic(){
        boolean playMusic = BAPMPreferences.INSTANCE.getAutoPlayMusic(context);
        if (playMusic) {
            mPlayMusicControl.pause();
        }
    }

    private void sendAppToBackground(LaunchAppHelper launchAppHelper){
        boolean sendToBackground = BAPMPreferences.INSTANCE.getSendToBackground(context);
        if (sendToBackground) {
            launchAppHelper.sendEverythingToBackground(context);
        }
    }

    private void turnOffPriorityMode(RingerControl ringerControl){

        boolean priorityMode = BAPMPreferences.INSTANCE.getPriorityMode(context);
        if (priorityMode) {
            int currentRinger = BAPMDataPreferences.INSTANCE.getCurrentRingerSet(context);
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

    private void closeWaze(LaunchAppHelper launchAppHelper){
        boolean closeWaze = BAPMPreferences.INSTANCE.getCloseWazeOnDisconnect(context)
                && launchAppHelper.checkPkgOnPhone(context, PackageTools.PackageName.WAZE)
                && BAPMPreferences.INSTANCE.getMapsChoice(context).equals(PackageTools.PackageName.WAZE);
        if(closeWaze) {
            launchAppHelper.closeWazeOnDisconnect(context);
        }
    }

    private void setWifiOn(LaunchAppHelper launchAppHelper){
        boolean isWifiOffDevice = BAPMDataPreferences.INSTANCE.getIsTurnOffWifiDevice(context);
        if(isWifiOffDevice){
            int eveningStartTime = BAPMPreferences.INSTANCE.getEveningStartTime(context);
            int eveningEndTime = BAPMPreferences.INSTANCE.getEveningEndTime(context);

            int current24hrTime = TimeHelper.getCurrent24hrTime();

            TimeHelper timeHelperEvening = new TimeHelper(eveningStartTime, eveningEndTime, current24hrTime);
            boolean canLaunch = timeHelperEvening.isWithinTimeSpan();
            String directionLocation = canLaunch ? LaunchAppHelper.DirectionLocations.HOME : LaunchAppHelper.DirectionLocations.WORK;

            boolean canChangeWifiState = !BAPMPreferences.INSTANCE.getWifiUseMapTimeSpans(context)
                    || (canLaunch && launchAppHelper.canLaunchOnThisDay(context, directionLocation));
            if(canChangeWifiState && !WifiControl.INSTANCE.isWifiON(context)) {
                WifiControl.INSTANCE.wifiON(context, true);
            }
            BAPMDataPreferences.INSTANCE.setIsTurnOffWifiDevice(context, false);
        }
    }

    private void stopKeepingScreenOn(){
        boolean screenON = BAPMPreferences.INSTANCE.getKeepScreenON(context);
        if (screenON) {
            ServiceUtils.INSTANCE.stopService(context, WakeLockService.class, WakeLockService.TAG);
        }
    }

    private void setVolumeBack(RingerControl ringerControl){
        boolean volumeMAX = BAPMPreferences.INSTANCE.getMaxVolume(context);
        boolean setOriginalVolume = BAPMPreferences.INSTANCE.getRestoreNotificationVolume(context);

        if (volumeMAX && setOriginalVolume) {
            mVolumeControl.setToOriginalVolume(ringerControl);
        }
    }
}
