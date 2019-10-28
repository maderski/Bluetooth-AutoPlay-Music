package maderski.bluetoothautoplaymusic.controls.mediaplayer

import android.content.Context
import android.media.AudioManager
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.KeyEventControl
import maderski.bluetoothautoplaymusic.controls.playattempters.BasicPlayAttempter
import maderski.bluetoothautoplaymusic.controls.playattempters.PlayTaskHolder
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControlsFactory
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
    private val playerControls = PlayerControlsFactory.getPlayerControl(context, selectedMusicPlayerPackageName)

    fun play() {
        // Initial attempt to play
        val firstAttempt = PlayTaskHolder {
            Log.d(TAG, "ATTEMPT MEDIA CONTROLLER HELPER PLAY")
            mediaControllerHelper?.play()
        }
        // If after delay and music still isn't playing try to play with player controls
        val secondAttempt = PlayTaskHolder {
            if (!audioManager.isMusicActive) {
                Log.d(TAG, "ATTEMPT PLAYER CONTROLS PLAY")
                playerControls?.play()
            } else {
                playAttempter.cancelPlayAgain()
            }
        }
        // If after delay and music still isn't playing launch music player
        val thirdAttempt = PlayTaskHolder {
            if (!audioManager.isMusicActive) {
                Log.d(TAG, "ATTEMPT LAUNCHING APP")
                launchAppHelper.launchApp(selectedMusicPlayerPackageName)
            } else {
                playAttempter.cancelPlayAgain()
            }
        }
        // If music still isn't playing try using mediaController again since app should be open now
        val fourthAttempt = PlayTaskHolder {
            if (!audioManager.isMusicActive) {
                Log.d(TAG, "ATTEMPT MEDIA CONTROLLER HELPER PLAY")
                mediaControllerHelper?.play()
            } else {
                playAttempter.cancelPlayAgain()
            }
        }

        // If music still isn't playing try using MediaButton Play
        val finalAttempt = PlayTaskHolder {
            if (!audioManager.isMusicActive) {
                Log.d(TAG, "ATTEMPT KEY EVENT CONTROL PLAY")
                keyEventControl.playMediaButton(selectedMusicPlayerPackageName)
            }
        }
        // Queue up play attempts
        val playTasks = listOf(
                firstAttempt,
                secondAttempt,
                thirdAttempt,
                fourthAttempt,
                finalAttempt
        )
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

    companion object {
        private const val TAG = "MediaPlayerControlManag"
    }
}