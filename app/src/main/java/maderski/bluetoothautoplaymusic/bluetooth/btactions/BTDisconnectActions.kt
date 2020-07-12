package maderski.bluetoothautoplaymusic.bluetooth.btactions

import android.media.AudioManager
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.WAZE
import maderski.bluetoothautoplaymusic.launchers.MapAppLauncher
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.WakeLockService
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager

/**
 * Created by Jason on 6/3/17.
 */

class BTDisconnectActions(
        private val mVolumeControl: VolumeControl,
        private val launchHelper: LaunchHelper,
        private val ringerControl: RingerControl,
        private val mediaPlayerControlManager: MediaPlayerControlManager,
        private val serviceManager: ServiceManager,
        private val preferencesHelper: PreferencesHelper,
        private val mapAppLauncher: MapAppLauncher
) {
    //Removes mNotification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    fun actionsOnBTDisconnect() {
        pauseMusic()
        turnOffPriorityMode()
        closeWaze()
        stopKeepingScreenOn()

        setVolumeBack()
    }

    private fun pauseMusic() {
        val playMusic = preferencesHelper.canAutoPlayMusic
        if (playMusic) {
            mediaPlayerControlManager.pause()
        }
    }

    private fun turnOffPriorityMode() {

        val priorityMode = preferencesHelper.priorityMode
        if (priorityMode) {
            val currentRinger = ringerControl.getCurrentRingerSetting()
            try {
                when (currentRinger) {
                    AudioManager.RINGER_MODE_SILENT -> Log.d(TAG, "Phone is on Silent")
                    AudioManager.RINGER_MODE_VIBRATE -> ringerControl.vibrateOnly()
                    AudioManager.RINGER_MODE_NORMAL -> ringerControl.soundsON()
                }
            } catch (e: Exception) {
                e.message?.let {
                    Log.e(TAG, it)
                }
            }
        }
    }

    private fun closeWaze() {
        val closeWaze = (preferencesHelper.shouldCloseWaze
                && launchHelper.isAbleToLaunch(WAZE.packageName)
                && preferencesHelper.mapAppChosen == WAZE.packageName)
        if (closeWaze) {
            mapAppLauncher.closeWazeOnDisconnect()
        }
    }

    private fun stopKeepingScreenOn() {
        val screenON = preferencesHelper.keepScreenON
        if (screenON) {
            serviceManager.stopService(WakeLockService::class.java, WakeLockService.TAG)
        }
    }

    private fun setVolumeBack() {
        val volumeMAX = preferencesHelper.volumeMAX
        val originalVolume = preferencesHelper.originalVolume

        if (volumeMAX && originalVolume) {
            mVolumeControl.setToOriginalVolume()
        }
    }

    companion object {
        private const val TAG = "BTDisconnectActions"
    }
}
