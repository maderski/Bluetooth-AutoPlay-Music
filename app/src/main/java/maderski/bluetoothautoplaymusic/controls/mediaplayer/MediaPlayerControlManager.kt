package maderski.bluetoothautoplaymusic.controls.mediaplayer

import android.content.Context
import android.media.AudioManager
import maderski.bluetoothautoplaymusic.controls.playattempers.BasicPlayAttempter
import maderski.bluetoothautoplaymusic.controls.playattempers.PlayTaskHolder
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
        private val playAttempter: BasicPlayAttempter
) : MediaControllerHelper.PlayBackStateCallback {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val selectedMusicPlayerPackageName = preferences.getPkgSelectedMusicPlayer()
    private val token = mediaSessionTokenHelper.getMediaSessionToken(selectedMusicPlayerPackageName)
    private val mediaControllerHelper = if (token != null) MediaControllerHelper(context, token, this) else null

    fun play() {
        // Initial attempt to play
        val firstAttempt = PlayTaskHolder { mediaControllerHelper?.play() }
        // If after delay and music still isn't playing launch music player
        val secondAttempt = PlayTaskHolder {
            if (!audioManager.isMusicActive) {
                launchAppHelper.launchApp(selectedMusicPlayerPackageName)
            }
        }
        // If music still isn't playing try using mediaController again since app should be open now
        val thirdAttempt = PlayTaskHolder { mediaControllerHelper?.play() }
        // If music still isn't playing try using MediaButton Play
        val finalAttempt = PlayTaskHolder {
            if (!audioManager.isMusicActive) {
                keyEventControl.playKeyEvent()
            }
        }
        // Queue up play attemps
        val playTasks = listOf(firstAttempt, secondAttempt, thirdAttempt, finalAttempt)
        // Start attempting to play
        playAttempter.attemptToPlay(playTasks)
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