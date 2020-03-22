package maderski.bluetoothautoplaymusic.ui.activities

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.ActivityNameConstants
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.wrappers.AndroidSystemServicesWrapper
import org.koin.android.ext.android.inject

class LaunchBAPMActivity : AppCompatActivity() {
    private val preferences: BAPMPreferences by inject()
    private val screenONLock: ScreenONLock by inject()
    private val launchHelper: LaunchHelper by inject()
    private val firebaseHelper: FirebaseHelper by inject()
    private val systemServicesWrapper: AndroidSystemServicesWrapper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_bapm)

        // Create Firebase Event
        firebaseHelper.activityLaunched(ActivityNameConstants.LAUNCH_BAPM)

        // Dismiss the keyguard
        dismissKeyGuard()

        // Hide the fake loading screen.  This is used to keep this activity alive while dismissing the keyguard
        sendHomeAppTimer(3)
    }

    //Dismiss the KeyGuard
    private fun dismissKeyGuard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val window = window
            if (!preferences.getKeepScreenON()) {
                screenONLock.enableWakeLock(this)
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
                screenONLock.releaseWakeLock()
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
        } else {
            val keyguardManager = systemServicesWrapper.keyguardManager
            keyguardManager.requestDismissKeyguard(this, object : KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                    Log.d(TAG, "KEYGUARD DISMISS CANCELLED")
                }
            })
        }
    }

    private fun sendHomeAppTimer(seconds: Int) {
        val launchMaps = preferences.getLaunchGoogleMaps()
        val launchPlayer = preferences.getLaunchMusicPlayer()

        if (!launchMaps && !launchPlayer) {
            val context = this
            val milliSeconds = seconds * 1000
            val handler = Handler()
            val runnable = Runnable {
                finish()
                launchHelper.launchDisconnectActivity()
            }
            handler.postDelayed(runnable, milliSeconds.toLong())
        }
    }

    companion object {
        private val TAG = "LaunchBAPMActivity"
    }
}
