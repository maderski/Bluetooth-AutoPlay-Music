package maderski.bluetoothautoplaymusic.controls

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.utils.PermissionUtils
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 12/8/15.
 */
class RingerControl(
        systemServicesWrapper: SystemServicesWrapper,
        private val preferencesHelper: PreferencesHelper) {

    private val audioManager = systemServicesWrapper.audioManager
    private val notificationManager = systemServicesWrapper.notificationManager

    //turns phone sounds OFF & initialize AudioManager
    fun soundsOFF() {
        val isAlreadySilent = audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT
        if (!isAlreadySilent) {
            val usePriorityMode = preferencesHelper.priorityMode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val hasNAPPermission = notificationManager.isNotificationPolicyAccessGranted
                if (usePriorityMode && hasNAPPermission) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                    Log.d(TAG, "RingerControl: " + "Priority")
                } else {
                    putPhoneInSilentMode()
                }
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
    fun soundsON(context: Context) {
        val usePriorityMode = preferencesHelper.priorityMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasNAPPermission = PermissionUtils.hasNotificationAccessPermission(context)
            if (usePriorityMode && hasNAPPermission) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                Log.d(TAG, "RingerControl: " + "Normal")
            } else {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                Log.d(TAG, "RingerControl: " + "Normal")
            }
        } else {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            Log.d(TAG, "RingerControl: " + "Normal")
        }
    }

    fun vibrateOnly() {
        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
    }

    fun ringerSetting(): Int {
        return audioManager.ringerMode
    }

    companion object {
        private const val TAG = "RingerControl"
    }
}
