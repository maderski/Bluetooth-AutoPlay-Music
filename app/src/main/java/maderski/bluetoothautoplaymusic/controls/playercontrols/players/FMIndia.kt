package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class FMIndia(
        private val context: Context,
        systemServicesWrapper: SystemServicesWrapper,
        private val packageName: String
) : PlayerControls(systemServicesWrapper) {

    override fun play() {
        Log.d(TAG, "Play Music")

        val downIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)
        downIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent)
        downIntent.setPackage(packageName)
        context.sendOrderedBroadcast(downIntent, null)

        val upIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY)
        upIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent)
        upIntent.setPackage(packageName)
        context.sendOrderedBroadcast(upIntent, null)
    }

    override fun pause() {
        // Do nothing
    }

    companion object {
        private const val TAG = "FMIndia"
    }
}