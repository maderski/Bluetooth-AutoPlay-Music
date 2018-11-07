package maderski.bluetoothautoplaymusic.bluetoothactions

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.util.Log
import android.widget.Toast

import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.PlayMusicControl
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/3/17.
 */

class BTHeadphonesActions(private val mContext: Context) {
    private val mPlayMusicControl: PlayMusicControl = PlayMusicControl(mContext)
    private val mVolumeControl: VolumeControl = VolumeControl(mContext)
    private val mAudioManager: AudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun connectActionsWithDelay() {
        val handler = Handler()
        val runnable = Runnable { connectActions() }
        handler.postDelayed(runnable, 4000)

        ServiceUtils.startService(mContext, BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    fun connectActions() {
        // Get headphone preferred volume
        val preferredVolume = BAPMPreferences.getHeadphonePreferredVolume(mContext)
        // Set headphone preferred volume
        mVolumeControl.setSpecifiedVolume(preferredVolume)
        // Start checking if music is playing
        mPlayMusicControl.checkIfPlaying(mContext, 8)
        Log.d(TAG, "HEADPHONE VOLUME SET TO:" + Integer.toString(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)))

        BAPMDataPreferences.setIsHeadphonesDevice(mContext, true)
        if (BuildConfig.DEBUG)
            Toast.makeText(mContext, "Music Playing", Toast.LENGTH_SHORT).show()
    }

    fun disconnectActions() {
        mPlayMusicControl.pause()
        PlayMusicControl.cancelCheckIfPlaying()
        if (mAudioManager.isMusicActive) {
            mPlayMusicControl.pause()
        }
        mVolumeControl.setToOriginalVolume(RingerControl(mContext))

        BAPMDataPreferences.setIsHeadphonesDevice(mContext, false)
        if (BuildConfig.DEBUG)
            Toast.makeText(mContext, "Music Paused", Toast.LENGTH_SHORT).show()

        ServiceUtils.stopService(mContext, BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    companion object {
        private const val TAG = "BTHeadphonesActions"
    }
}
