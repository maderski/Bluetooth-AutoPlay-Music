package maderski.bluetoothautoplaymusic.controls.mediaplayer

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.helpers.MediaControllerHelper
import maderski.bluetoothautoplaymusic.helpers.MediaSessionTokenHelper
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

class MediaPlayerControlManager(
        context: Context,
        mediaSessionTokenHelper: MediaSessionTokenHelper,
        preferences: BAPMPreferences,
        private val launchAppHelper: LaunchAppHelper,
        private val keyEventControl: KeyEventControl,
        private val playAttempter: PlayAttempter
) : MediaControllerHelper.PlayBackStateCallback {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val selectedMusicPlayerPackageName = preferences.getPkgSelectedMusicPlayer()
    private val token = mediaSessionTokenHelper.getMediaSessionToken(selectedMusicPlayerPackageName)
    private val mediaControllerHelper = if (token != null) MediaControllerHelper(context, token, this) else null

    fun play() {
        playAttempter.attemptToPlay({
            // Initial attempt to play
            mediaControllerHelper?.play()
        }, {
            // If after delay and music still isn't playing launch music player
            if (!audioManager.isMusicActive) {
                launchAppHelper.launchApp(selectedMusicPlayerPackageName)
            }
        }, {
            // If music still isn't playing try using MediaButton Play
            if (!audioManager.isMusicActive) {
                keyEventControl.playKeyEvent()
                playAttempter.cancelPlayAgain()
            }
        })
    }

    override fun onStartedPlaying() {
        playAttempter.cancelPlayAgain()
    }

    fun pause() {
        playAttempter.cancelPlayAgain()
        if (audioManager.isMusicActive) {
            mediaControllerHelper?.pause() ?: keyEventControl.pauseKeyEvent()
        }
    }
}