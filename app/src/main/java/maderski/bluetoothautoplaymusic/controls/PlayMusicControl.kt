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
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

/**
 * Created by Jason on 12/8/15.
 */
class PlayMusicControl(context: Context) {

    private val mPlayerControls: PlayerControls
    private val mFirebaseHelper: FirebaseHelper

    init {
        val pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context)
        Log.d(TAG, "PLAYER: $pkgName")

        mPlayerControls = PlayerControlsFactory.getPlayerControl(context, pkgName)
        mFirebaseHelper = FirebaseHelper(context)
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
        val selectedMusicPlayer = BAPMPreferences.getPkgSelectedMusicPlayer(context)
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
                    PackageHelper.PANDORA -> finalAttemptToPlayPandora(context)
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
        val launchAppHelper = LaunchAppHelper()
        launchAppHelper.launchPackage(context, PackageHelper.PANDORA)
        Log.d(TAG, "PANDORA LAUNCHED")

        val handler = Handler()
        val runnable = Runnable {
            if (BAPMPreferences.getLaunchGoogleMaps(context)) {
                val choosenMapApp = BAPMPreferences.getMapsChoice(context)
                launchAppHelper.launchPackage(context, choosenMapApp)
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

