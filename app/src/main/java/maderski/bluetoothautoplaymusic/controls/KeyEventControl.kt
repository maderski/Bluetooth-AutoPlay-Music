package maderski.bluetoothautoplaymusic.controls

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class KeyEventControl(
        private val context: Context, systemServicesWrapper: SystemServicesWrapper
) {
    private val audioManager: AudioManager = systemServicesWrapper.audioManager

    @Synchronized
    fun playKeyEvent() {
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)
        audioManager.dispatchMediaKeyEvent(downEvent)

        val upEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    @Synchronized
    fun pauseKeyEvent() {
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE)
        audioManager.dispatchMediaKeyEvent(downEvent)

        val upEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    @Synchronized
    fun playPauseKeyEvent() {
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        audioManager.dispatchMediaKeyEvent(downEvent)

        val upEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    @Synchronized
    fun playMediaButton(packageName: String) {
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
}