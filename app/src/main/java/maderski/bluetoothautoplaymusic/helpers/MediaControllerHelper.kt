package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.mediaplayer.PlayBackStateCallback

class MediaControllerHelper(
        context: Context,
        token: MediaSession.Token
): MediaController.Callback() {
    private val mediaController = MediaController(context, token)

    fun play() = mediaController.transportControls.play()
    fun pause() = mediaController.transportControls.pause()
    fun stop() = mediaController.transportControls.stop()

    fun isPlaying(): Boolean = mediaController.playbackState?.state == PlaybackState.STATE_PLAYING
    fun isPaused(): Boolean = mediaController.playbackState?.state == PlaybackState.STATE_PAUSED
    fun isStopped(): Boolean = mediaController.playbackState?.state == PlaybackState.STATE_STOPPED

    fun getPlayerCurrentVolume(): Int = mediaController.playbackInfo?.currentVolume ?: VOLUME_UNKNOWN
    fun getPlayerMaxVolume(): Int = mediaController.playbackInfo?.maxVolume ?: VOLUME_UNKNOWN

    override fun onPlaybackStateChanged(state: PlaybackState?) {
        super.onPlaybackStateChanged(state)
        Log.d(TAG, "PLAYBACK STATE: $state")
        state?.let { playbackState ->
            if (playbackState.state == PlaybackState.STATE_PLAYING) {
                // TODO: See if I need to call register this callback with the MediaController
            }
        }
    }

    companion object {
        private const val TAG = "MediaControllerHelper"
        const val VOLUME_UNKNOWN = -1
    }
}