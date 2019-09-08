package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState

class MediaControllerHelper(
        context: Context,
        token: MediaSession.Token,
        private val callback: PlayBackStateCallback
): MediaController.Callback() {
    interface PlayBackStateCallback {
        fun onStartedPlaying()
    }
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
        state?.let { playbackState ->
            if (playbackState.state == PlaybackState.STATE_PLAYING) {
                callback.onStartedPlaying()
            }
        }
    }

    companion object {
        const val VOLUME_UNKNOWN = -1
    }
}