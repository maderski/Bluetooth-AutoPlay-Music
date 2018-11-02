package maderski.bluetoothautoplaymusic.controls

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.util.Log

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

/**
 * Created by Jason on 4/2/16.
 */
class VolumeControl(private val mContext: Context) {

    private val am: AudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
        BAPMDataPreferences.setOriginalMediaVolume(mContext, originalVolume)
    }

    // Set to specified media volume
    fun setSpecifiedVolume(volumeValue: Int) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, AudioManager.FLAG_SHOW_UI)
        Log.d(TAG, "SET VOLUME TO: " + volumeValue.toString()
                + "MAX VOL: " + am.getStreamMaxVolume(mStreamType).toString())
    }

    // Set original media volume
    fun setToOriginalVolume(ringerControl: RingerControl) {
        val originalMediaVolume = BAPMDataPreferences.getOriginalMediaVolume(mContext)

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
            BAPMDataPreferences.setOriginalMediaVolume(mContext, am.getStreamVolume(AudioManager.STREAM_MUSIC))

            Log.d(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(mContext)))

            val launchIntent = Intent()
            launchIntent.action = "maderski.bluetoothautoplaymusic.offtelephonelaunch"
            mContext.sendBroadcast(launchIntent)
        }

        handler.postDelayed(runnable, milliseconds.toLong())
    }

    private fun setToMaxVol() {
        val maxVolume = BAPMPreferences.getUserSetMaxVolume(mContext)
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
