package maderski.bluetoothautoplaymusic.bluetooth.btactions

import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.*
import maderski.bluetoothautoplaymusic.common.AppScope
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.maps.MapApps.WAZE
import maderski.bluetoothautoplaymusic.maps.MapLauncherFactory
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
        private val mapLauncherFactory: MapLauncherFactory
) : CoroutineScope by AppScope() {
    //Removes mNotification and if set releases wakelock, puts the ringer back to normal,
    //pauses the music
    fun actionsOnBTDisconnect() {
        launch {
            withContext(Dispatchers.Default) {
                pauseMusic()
                turnOffPriorityMode()
                closeWaze()
                stopKeepingScreenOn()
                delay(1000)
                setVolumeBack()
            }
        }
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
                && preferencesHelper.mapAppName == WAZE.packageName)
        if (closeWaze) {
            val mapAppLauncher = mapLauncherFactory.getMapLauncher(WAZE)
            mapAppLauncher.closeMaps()
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
