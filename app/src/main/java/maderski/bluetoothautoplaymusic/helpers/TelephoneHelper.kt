package maderski.bluetoothautoplaymusic.helpers

import android.os.CountDownTimer
import android.telephony.TelephonyManager
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.wrappers.AndroidSystemServicesWrapper

/**
 * Created by Jason on 6/1/16.
 */
class TelephoneHelper(
        systemServicesWrapper: AndroidSystemServicesWrapper,
        private val powerHelper: PowerHelper,
        private val btConnectActions: BTConnectActions,
        private val firebaseHelper: FirebaseHelper
) {
    private val telephonyManager = systemServicesWrapper.telephonyManager
    val isOnCall: Boolean
        get() {
            val currentCallState = telephonyManager.callState

            return if (currentCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
                Log.d(TAG, "ON CALL!")
                true
            } else {
                Log.d(TAG, "Not on Call")
                false
            }
        }

    fun checkIfOnPhone(volumeControl: VolumeControl) {

        val totalSeconds = 43200000 //check for 12 hours
        val countDownInterval = 2000 //2 second interval

        object : CountDownTimer(totalSeconds.toLong(), countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                if (powerHelper.isPluggedIn()) {
                    if (isOnCall) {
                        Log.d(TAG, "On Call, check again in 3 sec")
                    } else {
                        Log.d(TAG, "Off Call, Launching Bluetooth Autoplay music")
                        cancel()
                        //Get Original Volume and Launch Bluetooth Autoplay Music
                        volumeControl.delayGetOrigVol(3) {
                            //Calling actionsOnBTConnect cause onBTConnect already ran
                            btConnectActions.actionsOnBTConnect()
                            firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.TELEPHONE)
                        }
                    }
                } else {
                    //Bailing cause phone is not plugged in
                    Log.d(TAG, "Phone is no longer plugged in to power")
                    cancel()
                }
            }

            override fun onFinish() {}
        }.start()
    }

    companion object {
        private const val TAG = "TelephoneHelper"
    }
}
