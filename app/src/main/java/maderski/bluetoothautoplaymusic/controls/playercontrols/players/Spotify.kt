package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MediaPlayers.SPOTIFY

class Spotify(context: Context) : PlayerControls(context) {

    override fun play() {
        Log.d(TAG, "Play Music")
        val downIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        downIntent.component = ComponentName(SPOTIFY.packageName, "com.spotify.music.internal.receiver.MediaButtonReceiver")
        downIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY))
        context.sendOrderedBroadcast(downIntent, null)

        val upIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        upIntent.component = ComponentName(SPOTIFY.packageName, "com.spotify.music.internal.receiver.MediaButtonReceiver")
        upIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY))
        context.sendOrderedBroadcast(upIntent, null)

    }

    companion object {
        private const val TAG = "Spotify"
    }
}