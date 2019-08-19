package maderski.bluetoothautoplaymusic.controls

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log

import maderski.bluetoothautoplaymusic.utils.PermissionUtils
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 12/8/15.
 */
class RingerControl(private val mContext: Context) : KoinComponent {
    private val preferences: BAPMPreferences by inject()

    private val am: AudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mNotificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    //turns phone sounds OFF & initialize AudioManager
    fun soundsOFF() {
        val isAlreadySilent = am.ringerMode == AudioManager.RINGER_MODE_SILENT
        if (!isAlreadySilent) {
            val usePriorityMode = preferences.getUsePriorityMode()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val hasNAPPermission = mNotificationManager.isNotificationPolicyAccessGranted
                if (usePriorityMode && hasNAPPermission) {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
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
        am.ringerMode = AudioManager.RINGER_MODE_SILENT
        Log.d(TAG, "RingerControl: " + "Silent")
    }

    //turns phone sounds ON
    fun soundsON() {
        val usePriorityMode = preferences.getUsePriorityMode()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = Manifest.permission.ACCESS_NOTIFICATION_POLICY
            val hasNAPPermission = PermissionUtils.isPermissionGranted(mContext, permission)
            if (usePriorityMode && hasNAPPermission) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                Log.d(TAG, "RingerControl: " + "Normal")
            } else {
                am.ringerMode = AudioManager.RINGER_MODE_NORMAL
                Log.d(TAG, "RingerControl: " + "Normal")
            }
        } else {
            am.ringerMode = AudioManager.RINGER_MODE_NORMAL
            Log.d(TAG, "RingerControl: " + "Normal")
        }
    }

    fun vibrateOnly() {
        am.ringerMode = AudioManager.RINGER_MODE_VIBRATE
    }

    fun ringerSetting(): Int {
        return am.ringerMode
    }

    companion object {
        private const val TAG = "RingerControl"
    }
}
