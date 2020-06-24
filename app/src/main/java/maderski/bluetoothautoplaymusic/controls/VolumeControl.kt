package maderski.bluetoothautoplaymusic.controls

import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 4/2/16.
 */
class VolumeControl(
        systemServicesWrapper: SystemServicesWrapper,
        private val preferencesHelper: PreferencesHelper
) {
    private val audioManager: AudioManager = systemServicesWrapper.audioManager

    private var originalMediaVolume: Int = 0

    // Set Mediavolume to MAX
    fun volumeMAX() {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        Log.d(TAG, "Max Media Volume is: $maxVolume")
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI)
    }

    fun saveOriginalVolume() {
        originalMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
    }

    // Set to specified media volume
    fun setSpecifiedVolume(volumeValue: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, AudioManager.FLAG_SHOW_UI)
        Log.d(TAG, "SET VOLUME TO: " + volumeValue.toString()
                + "MAX VOL: " + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toString())
    }

    // Set original media volume
    fun setToOriginalVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalMediaVolume, AudioManager.FLAG_SHOW_UI)
        Log.d(TAG, "Media Volume is set to: $originalMediaVolume")
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
        setToMaxVol()

        val milliseconds = seconds * 1000
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ setToMaxVol() }, milliseconds.toLong())
    }

    fun getDeviceMaxVolume(): Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    companion object {
        private const val TAG = "VolumeControl"
    }
}
