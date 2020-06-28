package maderski.bluetoothautoplaymusic.controls

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 12/8/15.
 */
class RingerControl(
        private val preferencesHelper: PreferencesHelper,
        private val permissionManager: PermissionManager,
        systemServicesWrapper: SystemServicesWrapper
) {

    private val audioManager = systemServicesWrapper.audioManager
    private val notificationManager = systemServicesWrapper.notificationManager

    private var currentRingerSetting = AudioManager.RINGER_MODE_NORMAL
    //turns phone sounds OFF & initialize AudioManager
    fun soundsOFF() {
        val isAlreadySilent = audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT
        if (!isAlreadySilent) {
            val usePriorityMode = preferencesHelper.priorityMode
            val hasNAPPermission = notificationManager.isNotificationPolicyAccessGranted
            if (usePriorityMode && hasNAPPermission) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                Log.d(TAG, "RingerControl: " + "Priority")
            } else {
                putPhoneInSilentMode()
            }
        } else {
            Log.d(TAG, "Ringer is Already silent")
        }
    }

    private fun putPhoneInSilentMode() {
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
        Log.d(TAG, "RingerControl: " + "Silent")
    }

    //turns phone sounds ON
    fun soundsON() {
        val usePriorityMode = preferencesHelper.priorityMode
        val hasNAPPermission = permissionManager.hasNotificationAccessPermission()
        if (usePriorityMode && hasNAPPermission) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            Log.d(TAG, "RingerControl: " + "Normal")
        } else {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            Log.d(TAG, "RingerControl: " + "Normal")
        }
    }

    fun vibrateOnly() {
        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
    }

    fun saveCurrentRingerSetting() {
        currentRingerSetting = audioManager.ringerMode
    }

    fun getCurrentRingerSetting() = currentRingerSetting

    companion object {
        private const val TAG = "RingerControl"
    }
}
