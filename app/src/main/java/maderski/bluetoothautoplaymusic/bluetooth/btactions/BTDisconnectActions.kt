package maderski.bluetoothautoplaymusic.bluetooth.btactions

import android.content.Context
import android.media.AudioManager
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.WifiControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import maderski.bluetoothautoplaymusic.helpers.enums.DirectionLocation
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.WAZE
import maderski.bluetoothautoplaymusic.launchers.MapAppLauncher
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.WakeLockService
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/3/17.
 */

class BTDisconnectActions(private val context: Context): KoinComponent {
    private val mBAPMNotification: BAPMNotification by inject()
    private val mVolumeControl: VolumeControl by inject()
    private val launchHelper: LaunchHelper by inject()
    private val ringerControl: RingerControl by inject()
    private val mediaPlayerControlManager: MediaPlayerControlManager by inject()
    private val serviceManager: ServiceManager by inject()
    private val preferencesHelper: PreferencesHelper by inject()
    private val mapAppLauncher: MapAppLauncher by inject()

    //Removes mNotification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    fun actionsOnBTDisconnect() {
        removeBAPMNotification()
        pauseMusic()
        turnOffPriorityMode(ringerControl)
        closeWaze(launchHelper)
        setWifiOn(launchHelper)
        stopKeepingScreenOn()

        setVolumeBack(ringerControl)
    }

    private fun removeBAPMNotification() {
        val canShowNotification = preferencesHelper.canShowNotification

        if (canShowNotification) {
            mBAPMNotification.removeBAPMMessage()
        }
    }

    private fun pauseMusic() {
        val playMusic = preferencesHelper.canAutoPlayMusic
        if (playMusic) {
            mediaPlayerControlManager.pause()
        }
    }

    private fun turnOffPriorityMode(ringerControl: RingerControl) {

        val priorityMode = preferencesHelper.priorityMode
        if (priorityMode) {
            val currentRinger = preferencesHelper.currentRingerSet
            try {
                when (currentRinger) {
                    AudioManager.RINGER_MODE_SILENT -> Log.d(TAG, "Phone is on Silent")
                    AudioManager.RINGER_MODE_VIBRATE -> ringerControl.vibrateOnly()
                    AudioManager.RINGER_MODE_NORMAL -> ringerControl.soundsON(context)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }

        }
    }

    private fun closeWaze(launchHelper: LaunchHelper) {
        val closeWaze = (preferencesHelper.shouldCloseWaze
                && launchHelper.isAbleToLaunch(WAZE.packageName)
                && preferencesHelper.mapAppChosen == WAZE.packageName)
        if (closeWaze) {
            mapAppLauncher.closeWazeOnDisconnect()
        }
    }

    private fun setWifiOn(launchHelper: LaunchHelper) {
        val isWifiOffDevice = preferencesHelper.isWifiOffDevice
        if (isWifiOffDevice) {
            val eveningStartTime = preferencesHelper.eveningStartTime
            val eveningEndTime = preferencesHelper.eveningEndTime

            val current24hrTime = TimeHelper.current24hrTime

            val timeHelperEvening = TimeHelper(eveningStartTime, eveningEndTime, current24hrTime)
            val canLaunch = timeHelperEvening.isWithinTimeSpan
            val directionLocation = if (canLaunch) DirectionLocation.HOME else DirectionLocation.WORK

            val canChangeWifiState = !preferencesHelper.isUsingWifiMapTimeSpans || canLaunch && mapAppLauncher.canLaunchOnThisDay(directionLocation)
            if (canChangeWifiState && !WifiControl.isWifiON(context)) {
                WifiControl.wifiON(context, true)
            }
            preferencesHelper.isWifiOffDevice = false
        }
    }

    private fun stopKeepingScreenOn() {
        val screenON = preferencesHelper.keepScreenON
        if (screenON) {
            serviceManager.stopService(WakeLockService::class.java, WakeLockService.TAG)
        }
    }

    private fun setVolumeBack(ringerControl: RingerControl) {
        val volumeMAX = preferencesHelper.volumeMAX
        val originalVolume = preferencesHelper.originalVolume

        if (volumeMAX && originalVolume) {
            mVolumeControl.setToOriginalVolume(ringerControl)
        }
    }

    companion object {
        private const val TAG = "BTDisconnectActions"
    }
}
