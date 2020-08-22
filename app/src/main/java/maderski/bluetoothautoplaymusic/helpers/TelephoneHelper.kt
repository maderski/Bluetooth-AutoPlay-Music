package maderski.bluetoothautoplaymusic.helpers

import android.media.AudioManager
import android.os.CountDownTimer
import android.telephony.TelephonyManager
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 6/1/16.
 */
class TelephoneHelper(
        systemServicesWrapper: SystemServicesWrapper,
        private val firebaseHelper: FirebaseHelper
) {
    private val telephonyManager = systemServicesWrapper.telephonyManager

    fun isOnCall(): Boolean {
        val currentCallState = telephonyManager.callState
        return if (currentCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
            Log.d(TAG, "ON CALL!")
            true
        } else {
            Log.d(TAG, "Not on Call")
            false
        }
    }

    fun checkIfOnPhone(volumeControl: VolumeControl, ringerControl: RingerControl) {

        val totalSeconds = 43200000 // check for 12 hours
        val countDownInterval = 2000 // 2 second interval

        object : CountDownTimer(totalSeconds.toLong(), countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                if (isOnCall()) {
                    Log.d(TAG, "On Call, check again in 2 sec")
                } else {
                    Log.d(TAG, "Off Call, Launching Bluetooth Autoplay music")
                    cancel()
                    val isRingerNotOnSilent = ringerControl.getCurrentRingerSetting() != AudioManager.RINGER_MODE_SILENT
                    if (isRingerNotOnSilent) {
                        // Save the Original Volume and Launch Bluetooth Autoplay Music
                        volumeControl.saveOriginalVolume()
                    }
                    // Calling actionsOnBTConnect cause onBTConnect already ran
                    //btConnectActions.actionsOnBTConnect()
                    firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.TELEPHONE)
                }
            }

            override fun onFinish() {}
        }.start()
    }

    companion object {
        private const val TAG = "TelephoneHelper"
    }
}
