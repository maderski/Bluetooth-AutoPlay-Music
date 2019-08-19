package maderski.bluetoothautoplaymusic.controls

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.util.Log

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControlsFactory
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MediaPlayers.*
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 12/8/15.
 */
class PlayMusicControl(context: Context) : KoinComponent {
    private val preferences: BAPMPreferences by inject()

    private val mPlayerControls: PlayerControls
    private val mFirebaseHelper: FirebaseHelper = FirebaseHelper(context)

    init {
        val pkgName = preferences.getPkgSelectedMusicPlayer()
        Log.d(TAG, "PLAYER: $pkgName")

        mPlayerControls = PlayerControlsFactory.getPlayerControl(context, pkgName)
    }

    fun pause() {
        mPlayerControls.pause()
    }

    fun play() {
        Log.d(TAG, "Tried to play")
        mPlayerControls.play()
    }

    @Synchronized
    fun checkIfPlaying(context: Context, seconds: Int) {
        val milliSeconds = seconds * 1000
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val selectedMusicPlayer = preferences.getPkgSelectedMusicPlayer()
        Handler().postDelayed({
            if (!audioManager.isMusicActive) {
                play()
            }
        }, 3000)

        mHandler = Handler()
        mRunnable = Runnable {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "isMusicPlaying: " + java.lang.Boolean.toString(audioManager.isMusicActive))
            }

            if (!audioManager.isMusicActive) {
                when (selectedMusicPlayer) {
                    PANDORA.packageName -> finalAttemptToPlayPandora(context)
                    else -> {
                        Log.d(TAG, "Play media Button")
                        mPlayerControls.playMediaButton(selectedMusicPlayer)
                    }
                }
            }

            mFirebaseHelper.musicAutoPlay(audioManager.isMusicActive)
        }
        mHandler?.postDelayed(mRunnable, milliSeconds.toLong())
    }

    private fun finalAttemptToPlayPandora(context: Context) {
        val launchAppHelper = LaunchAppHelper(context)
        launchAppHelper.launchApp(PANDORA.packageName)
        Log.d(TAG, "PANDORA LAUNCHED")

        val handler = Handler()
        val runnable = Runnable {
            if (preferences.getLaunchGoogleMaps()) {
                val choosenMapApp = preferences.getMapsChoice()
                launchAppHelper.launchApp(choosenMapApp)
            }
        }
        handler.postDelayed(runnable, 4000)
    }

    companion object {
        private const val TAG = "PlayMusicControl"

        private var mHandler: Handler? = null
        private var mRunnable: Runnable? = null

        fun cancelCheckIfPlaying(): Boolean {
            if (mHandler != null && mRunnable != null) {
                mHandler?.removeCallbacks(mRunnable)
                Log.d(TAG, "Music Play Check CANCELLED")
                mHandler = null
                mRunnable = null
            }
            return false
        }
    }
}

