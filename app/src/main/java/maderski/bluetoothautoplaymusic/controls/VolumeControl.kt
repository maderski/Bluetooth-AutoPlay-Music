package maderski.bluetoothautoplaymusic.controls

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.helpers.AndroidSystemServicesHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 4/2/16.
 */
class VolumeControl(
        androidSystemServicesHelper: AndroidSystemServicesHelper,
        private val preferencesHelper: PreferencesHelper
) {
    private val audioManager: AudioManager = androidSystemServicesHelper.audioManager
    private val mStreamType: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                AudioManager.STREAM_NOTIFICATION
            else
                AudioManager.STREAM_MUSIC

    // Set Mediavolume to MAX
    fun volumeMAX() {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        Log.d(TAG, "Max Media Volume is: $maxVolume")
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI)
    }

    fun saveOriginalVolume() {
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        preferencesHelper.originalMediaVolume = originalVolume
    }

    // Set to specified media volume
    fun setSpecifiedVolume(volumeValue: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, AudioManager.FLAG_SHOW_UI)
        Log.d(TAG, "SET VOLUME TO: " + volumeValue.toString()
                + "MAX VOL: " + audioManager.getStreamMaxVolume(mStreamType).toString())
    }

    // Set original media volume
    fun setToOriginalVolume(ringerControl: RingerControl) {
        val originalMediaVolume = preferencesHelper.originalMediaVolume

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ringerControl.ringerSetting() != AudioManager.RINGER_MODE_SILENT) {
                audioManager.setStreamVolume(mStreamType, originalMediaVolume, AudioManager.FLAG_SHOW_UI)
                Log.d(TAG, "Media Volume is set to: $originalMediaVolume")
            } else {
                Log.d(TAG, "Did NOT set Media Volume")
            }
        } else {
            audioManager.setStreamVolume(mStreamType, originalMediaVolume, AudioManager.FLAG_SHOW_UI)
            Log.d(TAG, "Media Volume is set to: $originalMediaVolume")
        }
    }

    //Wait 3 seconds before getting the Original Volume
    fun delayGetOrigVol(seconds: Int) {
        val milliseconds = seconds * 1000
        val handler = Handler()
        val runnable = Runnable {
            preferencesHelper.originalMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

            Log.d(TAG, "Original Media Volume is: " + preferencesHelper.originalMediaVolume.toString())
        }

        handler.postDelayed(runnable, milliseconds.toLong())
    }

    private fun setToMaxVol() {
        val deviceMaxVolume = getDeviceMaxVolume()
        val maxVolume = preferencesHelper.getUserSetMaxVolume(deviceMaxVolume)
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != maxVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI)
            Log.d(TAG, "Set Volume To MAX")
        } else if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == maxVolume) {
            Log.d(TAG, "Volume is at MAX!")
        }
    }

    fun checkSetMAXVol(seconds: Int) {
        val milliseconds = seconds * 1000

        setToMaxVol()

        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable { setToMaxVol() }
        handler.postDelayed(runnable, milliseconds.toLong())
    }

    fun getDeviceMaxVolume(): Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    companion object {
        private const val TAG = "VolumeControl"
    }
}
