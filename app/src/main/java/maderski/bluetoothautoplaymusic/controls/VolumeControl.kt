package maderski.bluetoothautoplaymusic.controls

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetoothactions.BTConnectActions

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

/**
 * Created by Jason on 4/2/16.
 */
class VolumeControl(private val context: Context) {

    private val am: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mStreamType: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                AudioManager.STREAM_NOTIFICATION
            else
                AudioManager.STREAM_MUSIC

    // Set Mediavolume to MAX
    fun volumeMAX() {
        val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        Log.d(TAG, "Max Media Volume is: " + Integer.toString(maxVolume))
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI)
    }

    fun saveOriginalVolume() {
        val originalVolume = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        BAPMDataPreferences.setOriginalMediaVolume(context, originalVolume)
    }

    // Set to specified media volume
    fun setSpecifiedVolume(volumeValue: Int) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, AudioManager.FLAG_SHOW_UI)
        Log.d(TAG, "SET VOLUME TO: " + volumeValue.toString()
                + "MAX VOL: " + am.getStreamMaxVolume(mStreamType).toString())
    }

    // Set original media volume
    fun setToOriginalVolume(ringerControl: RingerControl) {
        val originalMediaVolume = BAPMDataPreferences.getOriginalMediaVolume(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ringerControl.ringerSetting() != AudioManager.RINGER_MODE_SILENT) {
                am.setStreamVolume(mStreamType, originalMediaVolume, AudioManager.FLAG_SHOW_UI)
                Log.d(TAG, "Media Volume is set to: " + Integer.toString(originalMediaVolume))
            } else {
                Log.d(TAG, "Did NOT set Media Volume")
            }
        } else {
            am.setStreamVolume(mStreamType, originalMediaVolume, AudioManager.FLAG_SHOW_UI)
            Log.d(TAG, "Media Volume is set to: " + Integer.toString(originalMediaVolume))
        }
    }

    //Wait 3 seconds before getting the Original Volume
    fun delayGetOrigVol(seconds: Int) {
        val milliseconds = seconds * 1000
        val handler = Handler()
        val runnable = Runnable {
            BAPMDataPreferences.setOriginalMediaVolume(context, am.getStreamVolume(AudioManager.STREAM_MUSIC))

            Log.d(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)))
            // TODO: This belongs somewhere else
            // Launch actionOnBTConnect cause off the Telephone
            val btConnectActions = BTConnectActions(context)
            val firebaseHelper = FirebaseHelper(context)
            //Calling actionsOnBTConnect cause onBTConnect already ran
            btConnectActions.actionsOnBTConnect()
            firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.TELEPHONE)
        }

        handler.postDelayed(runnable, milliseconds.toLong())
    }

    private fun setToMaxVol() {
        val maxVolume = BAPMPreferences.getUserSetMaxVolume(context)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) != maxVolume) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI)
            Log.d(TAG, "Set Volume To MAX")
        } else if (am.getStreamVolume(AudioManager.STREAM_MUSIC) == maxVolume) {
            Log.d(TAG, "Volume is at MAX!")
        }
    }

    fun checkSetMAXVol(seconds: Int) {
        val milliseconds = seconds * 1000

        setToMaxVol()

        val handler = Handler()
        val runnable = Runnable { setToMaxVol() }
        handler.postDelayed(runnable, milliseconds.toLong())
    }

    companion object {
        private const val TAG = "VolumeControl"

        fun getDeviceMaxVolume(context: Context): Int {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        }
    }
}
