package maderski.bluetoothautoplaymusic.ui.activities

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.ActivityNameConstants
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

class LaunchBAPMActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_bapm)

        // Create Firebase Event
        val firebaseHelper = FirebaseHelper(this)
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
            if (!BAPMPreferences.getKeepScreenON(this)) {
                val screenONLock = ScreenONLock.instance
                screenONLock.enableWakeLock(this)
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
                screenONLock.releaseWakeLock()
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
        } else {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, object : KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                    Log.d(TAG, "KEYGUARD DISMISS CANCELLED")
                }
            })
        }
    }

    private fun sendHomeAppTimer(seconds: Int) {
        val launchMaps = BAPMPreferences.getLaunchGoogleMaps(this)
        val launchPlayer = BAPMPreferences.getLaunchMusicPlayer(this)

        if (!launchMaps && !launchPlayer) {
            val context = this
            val milliSeconds = seconds * 1000
            val handler = Handler()
            val runnable = Runnable {
                finish()
                val launchAppHelper = LaunchAppHelper(this)
                launchAppHelper.sendEverythingToBackground(context)
            }
            handler.postDelayed(runnable, milliSeconds.toLong())
        }
    }

    companion object {
        private val TAG = "LaunchBAPMActivity"
    }
}
