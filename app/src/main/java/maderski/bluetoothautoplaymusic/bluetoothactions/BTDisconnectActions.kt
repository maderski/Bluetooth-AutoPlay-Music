package maderski.bluetoothautoplaymusic.bluetoothactions

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.util.Log

import maderski.bluetoothautoplaymusic.controls.PlayMusicControl
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.WifiControl
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.BAPMNotification
import maderski.bluetoothautoplaymusic.PackageTools
import maderski.bluetoothautoplaymusic.services.BTDisconnectService
import maderski.bluetoothautoplaymusic.services.WakeLockService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/3/17.
 */

class BTDisconnectActions(private val context: Context) {
    private val mBAPMNotification: BAPMNotification = BAPMNotification()
    private val mVolumeControl: VolumeControl = VolumeControl(context)
    private val mPlayMusicControl: PlayMusicControl = PlayMusicControl(context)

    //Removes mNotification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    fun actionsOnBTDisconnect() {
        val launchAppHelper = LaunchAppHelper()
        val ringerControl = RingerControl(context)

        removeBAPMNotification()
        pauseMusic()
        turnOffPriorityMode(ringerControl)
        sendAppToBackground(launchAppHelper)
        closeWaze(launchAppHelper)
        setWifiOn(launchAppHelper)
        stopKeepingScreenOn()

        setVolumeBack(ringerControl)

        stopService()
    }

    private fun stopService() {
        BAPMDataPreferences.setRanActionsOnBtConnect(context, false)
        Handler().postDelayed({ ServiceUtils.stopService(context, BTDisconnectService::class.java, BTDisconnectService.TAG) }, 5000)
    }

    private fun removeBAPMNotification() {
        val canShowNotification = BAPMPreferences.getShowNotification(context)

        if (canShowNotification) {
            mBAPMNotification.removeBAPMMessage(context)
        }
    }

    private fun pauseMusic() {
        val playMusic = BAPMPreferences.getAutoPlayMusic(context)
        if (playMusic) {
            mPlayMusicControl.pause()
        }
    }

    private fun sendAppToBackground(launchAppHelper: LaunchAppHelper) {
        val sendToBackground = BAPMPreferences.getSendToBackground(context)
        if (sendToBackground) {
            launchAppHelper.sendEverythingToBackground(context)
        }
    }

    private fun turnOffPriorityMode(ringerControl: RingerControl) {

        val priorityMode = BAPMPreferences.getPriorityMode(context)
        if (priorityMode) {
            val currentRinger = BAPMDataPreferences.getCurrentRingerSet(context)
            try {
                when (currentRinger) {
                    AudioManager.RINGER_MODE_SILENT -> Log.d(TAG, "Phone is on Silent")
                    AudioManager.RINGER_MODE_VIBRATE -> ringerControl.vibrateOnly()
                    AudioManager.RINGER_MODE_NORMAL -> ringerControl.soundsON()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }

        }
    }

    private fun closeWaze(launchAppHelper: LaunchAppHelper) {
        val closeWaze = (BAPMPreferences.getCloseWazeOnDisconnect(context)
                && launchAppHelper.checkPkgOnPhone(context, PackageTools.PackageName.WAZE)
                && BAPMPreferences.getMapsChoice(context) == PackageTools.PackageName.WAZE)
        if (closeWaze) {
            launchAppHelper.closeWazeOnDisconnect(context)
        }
    }

    private fun setWifiOn(launchAppHelper: LaunchAppHelper) {
        val isWifiOffDevice = BAPMDataPreferences.getIsTurnOffWifiDevice(context)
        if (isWifiOffDevice) {
            val eveningStartTime = BAPMPreferences.getEveningStartTime(context)
            val eveningEndTime = BAPMPreferences.getEveningEndTime(context)

            val current24hrTime = TimeHelper.current24hrTime

            val timeHelperEvening = TimeHelper(eveningStartTime, eveningEndTime, current24hrTime)
            val canLaunch = timeHelperEvening.isWithinTimeSpan
            val directionLocation = if (canLaunch) LaunchAppHelper.HOME else LaunchAppHelper.WORK

            val canChangeWifiState = BAPMPreferences.getWifiUseMapTimeSpans(context).not() || canLaunch && launchAppHelper.canLaunchOnThisDay(context, directionLocation)
            if (canChangeWifiState && !WifiControl.isWifiON(context)) {
                WifiControl.wifiON(context, true)
            }
            BAPMDataPreferences.setIsTurnOffWifiDevice(context, false)
        }
    }

    private fun stopKeepingScreenOn() {
        val screenON = BAPMPreferences.getKeepScreenON(context)
        if (screenON) {
            ServiceUtils.stopService(context, WakeLockService::class.java, WakeLockService.TAG)
        }
    }

    private fun setVolumeBack(ringerControl: RingerControl) {
        val volumeMAX = BAPMPreferences.getMaxVolume(context)
        val setOriginalVolume = BAPMPreferences.getRestoreNotificationVolume(context)

        if (volumeMAX && setOriginalVolume) {
            mVolumeControl.setToOriginalVolume(ringerControl)
        }
    }

    companion object {
        private const val TAG = "BTDisconnectActions"
    }
}
