package maderski.bluetoothautoplaymusic.BluetoothActions;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.Controls.RingerControl;
import maderski.bluetoothautoplaymusic.Controls.VolumeControl;
import maderski.bluetoothautoplaymusic.Controls.WifiControl;
import maderski.bluetoothautoplaymusic.Helpers.TimeHelper;
import maderski.bluetoothautoplaymusic.Helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.Notification;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.Services.BTDisconnectService;
import maderski.bluetoothautoplaymusic.Services.WakeLockService;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 6/3/17.
 */

public class BTDisconnectActions {
    private static final String TAG = "BTDisconnectActions";

    private final Context context;
    private final Notification mNotification;
    private final VolumeControl mVolumeControl;
    private final PlayMusicControl mPlayMusicControl;

    public BTDisconnectActions(Context context){
        this.context = context;
        this.mNotification = new Notification();
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
        BAPMDataPreferences.setRanActionsOnBtConnect(context, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ServiceUtils.stopService(context, BTDisconnectService.class, BTDisconnectService.TAG);
            }
        }, 5000);
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

    private void sendAppToBackground(LaunchAppHelper launchAppHelper){
        boolean sendToBackground = BAPMPreferences.getSendToBackground(context);
        if (sendToBackground) {
            launchAppHelper.sendEverythingToBackground(context);
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

    private void closeWaze(LaunchAppHelper launchAppHelper){
        boolean closeWaze = BAPMPreferences.getCloseWazeOnDisconnect(context)
                && launchAppHelper.checkPkgOnPhone(context, PackageTools.PackageName.WAZE)
                && BAPMPreferences.getMapsChoice(context).equals(PackageTools.PackageName.WAZE);
        if(closeWaze) {
            launchAppHelper.closeWazeOnDisconnect(context);
        }
    }

    private void setWifiOn(LaunchAppHelper launchAppHelper){
        boolean isWifiOffDevice = BAPMDataPreferences.getIsTurnOffWifiDevice(context);
        if(isWifiOffDevice){
            int eveningStartTime = BAPMPreferences.getEveningStartTime(context);
            int eveningEndTime = BAPMPreferences.getEveningEndTime(context);

            int current24hrTime = TimeHelper.getCurrent24hrTime();

            TimeHelper timeHelperEvening = new TimeHelper(eveningStartTime, eveningEndTime, current24hrTime);
            boolean canLaunch = timeHelperEvening.isWithinTimeSpan();
            String directionLocation = canLaunch ? LaunchAppHelper.DirectionLocations.HOME : LaunchAppHelper.DirectionLocations.WORK;

            boolean canChangeWifiState = !BAPMPreferences.getWifiUseMapTimeSpans(context)
                    || (canLaunch && launchAppHelper.canLaunchOnThisDay(context, directionLocation));
            if(canChangeWifiState && !WifiControl.isWifiON(context)) {
                WifiControl.wifiON(context, true);
            }
            BAPMDataPreferences.setIsTurnOffWifiDevice(context, false);
        }
    }

    private void stopKeepingScreenOn(){
        boolean screenON = BAPMPreferences.getKeepScreenON(context);
        if (screenON) {
            ServiceUtils.stopService(context, WakeLockService.class, WakeLockService.TAG);
        }
    }

    private void setVolumeBack(RingerControl ringerControl){
        boolean volumeMAX = BAPMPreferences.getMaxVolume(context);
        boolean setOriginalVolume = BAPMPreferences.getRestoreNotificationVolume(context);

        if (volumeMAX && setOriginalVolume) {
            mVolumeControl.setToOriginalVolume(ringerControl);
        }
    }
}
