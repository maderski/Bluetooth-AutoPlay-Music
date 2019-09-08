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
        private val keyEventControl: KeyEventControl
): MediaControllerHelper.PlayBackStateCallback {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val selectedMusicPlayerPackageName = preferences.getPkgSelectedMusicPlayer()
    private val token = mediaSessionTokenHelper.getMediaSessionToken(selectedMusicPlayerPackageName)
    private val mediaControllerHelper = if (token != null) MediaControllerHelper(context, token, this) else null

    private var playAgainHandler: Handler? = null
    private var playAgainRunnable: Runnable? = null

    fun play() {
        mediaControllerHelper?.play()
        startDelayedPlayAgain()
    }

    private fun startDelayedPlayAgain() {
        playAgainHandler = Handler(Looper.getMainLooper())
        playAgainRunnable = Runnable {
            // launch the app
            launchAppHelper.launchApp(selectedMusicPlayerPackageName)
            // attempt to play again after app has been launched
            playAgainHandler?.postDelayed({
                if (!audioManager.isMusicActive) {
                    keyEventControl.playMediaButton(selectedMusicPlayerPackageName)
                }
            }, DELAY)
        }
        playAgainHandler?.postDelayed(playAgainRunnable, DELAY)
    }

    private fun cancelDelayedPlayAgain() {
        if (playAgainHandler != null && playAgainRunnable != null) {
            playAgainHandler?.removeCallbacks(playAgainRunnable)
            playAgainHandler = null
            playAgainRunnable = null
        }
    }

    override fun onStartedPlaying() {
        cancelDelayedPlayAgain()
    }

    fun pause() {
        cancelDelayedPlayAgain()
        if (audioManager.isMusicActive) {
            mediaControllerHelper?.pause() ?: keyEventControl.pauseKeyEvent()
        }
    }

    companion object {
        const val DELAY = 2000L
    }
}