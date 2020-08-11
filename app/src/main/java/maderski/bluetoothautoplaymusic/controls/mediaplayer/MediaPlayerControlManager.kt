package maderski.bluetoothautoplaymusic.controls.mediaplayer

import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.KeyEventControl
import maderski.bluetoothautoplaymusic.controls.playattempters.BasicPlayAttempter
import maderski.bluetoothautoplaymusic.controls.playattempters.CoroutinePlayAttempter
import maderski.bluetoothautoplaymusic.controls.playattempters.PlayTaskHolder
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControlsFactory
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.MediaControllerHelper
import maderski.bluetoothautoplaymusic.helpers.MediaSessionTokenHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class MediaPlayerControlManager(
        private val context: Context,
        private val mediaSessionTokenHelper: MediaSessionTokenHelper,
        private val preferencesHelper: PreferencesHelper,
        private val systemServicesWrapper: SystemServicesWrapper,
        private val launchHelper: LaunchHelper,
        private val keyEventControl: KeyEventControl,
        private val playAttempter: CoroutinePlayAttempter,
        private val playerControlsFactory: PlayerControlsFactory
) {
    private val audioManager get() = systemServicesWrapper.audioManager
    private val selectedMusicPlayerPackageName get() = preferencesHelper.musicPlayerPkgName
    private val token get() = mediaSessionTokenHelper.getMediaSessionToken(selectedMusicPlayerPackageName)
    private val mediaControllerHelper get() = token?.let { MediaControllerHelper(context, it) }
    private val playerControls get() = playerControlsFactory.getPlayerControl(selectedMusicPlayerPackageName)

    private var playBackStateCallback: PlayBackStateCallback? = null
    fun play(callback: PlayBackStateCallback? = null) {
        // set callback if there is one
        playBackStateCallback = callback
        // Initial attempt to play
        val firstAttempt = PlayTaskHolder {
            Log.d(TAG, "ATTEMPT MEDIA CONTROLLER HELPER PLAY")
            attemptToPlay {
                mediaControllerHelper?.play()
            }
        }
        // If after delay and music still isn't playing launch music player
        val secondAttempt = PlayTaskHolder {
            attemptToPlay {
                Log.d(TAG, "ATTEMPT LAUNCHING APP")
                launchHelper.launchApp(selectedMusicPlayerPackageName)
            }
        }
        // If music still isn't playing try using mediaController again since app should be open now
        val thirdAttempt = PlayTaskHolder {
            attemptToPlay {
                Log.d(TAG, "ATTEMPT MEDIA CONTROLLER HELPER PLAY")
                mediaControllerHelper?.play()
            }
        }
        // If after delay and music still isn't playing try to play with player controls
        val fourthAttempt = PlayTaskHolder {
            attemptToPlay {
                Log.d(TAG, "ATTEMPT PLAYER CONTROLS PLAY")
                playerControls?.play()
            }
        }
        // If music still isn't playing try using MediaButton Play
        val finalAttempt = PlayTaskHolder {
            attemptToPlay {
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

    fun pause() {
        playAttempter.cancelPlayAgain()
        if (audioManager.isMusicActive) {
            mediaControllerHelper?.pause() ?: keyEventControl.pauseKeyEvent()
        }
    }

    private fun attemptToPlay(task: () -> Unit) {
        if (!audioManager.isMusicActive) {
            task()
        } else {
            Log.d(TAG, "Music is playing, canceling Play Again!")
            playAttempter.cancelPlayAgain()
            // Call onStarted playing callback
            playBackStateCallback?.onStartedPlaying()
        }
    }

    companion object {
        private const val TAG = "MediaPlayerControlManag"
    }
}