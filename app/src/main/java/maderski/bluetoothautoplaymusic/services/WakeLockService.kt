package maderski.bluetoothautoplaymusic.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 7/4/17.
 */

class WakeLockService : Service() {

    private var mScreenONLock: ScreenONLock? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Get wakelock instance
        mScreenONLock = ScreenONLock.instance

        // Release wakelock if it is still held for some reason
        mScreenONLock?.let { screenONLock ->
            if (screenONLock.wakeLockHeld()) {
                screenONLock.releaseWakeLock()
            }

            // Hold wakelock
            screenONLock.enableWakeLock(this)
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "WAKELOCK HELD", Toast.LENGTH_LONG).show()
            Log.d(TAG, "WAKELOCK SERVICE STARTED")
        }
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val title = getString(R.string.wakelock_messge)
        val message = getString(R.string.app_name)

        ServiceUtils.createServiceNotification(3453,
                title,
                message,
                this,
                ServiceUtils.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceUtils.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release wakelock
        mScreenONLock?.let {screenONLock ->
            if (screenONLock.wakeLockHeld()) {
                screenONLock.releaseWakeLock()
                if (BuildConfig.DEBUG) {
                    Toast.makeText(this, "WAKELOCK released", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "WAKELOCK SERVICE STOPPED")
                }
            }
        }

        stopForeground(true)
    }

    companion object {
        const val TAG = "WakeLockService"
    }
}