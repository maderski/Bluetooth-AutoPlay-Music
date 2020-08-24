package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls
import maderski.bluetoothautoplaymusic.helpers.enums.MediaPlayers.GOOGLE_PLAY_MUSIC
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class GooglePlayMusic(
        private val context: Context,
        systemServicesWrapper: SystemServicesWrapper
) : PlayerControls(systemServicesWrapper) {

    override fun play() {
        Log.d(TAG, "Play Music")
        val intent = Intent("com.android.music.musicservicecommand")
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra("command", "play")
        intent.setPackage(GOOGLE_PLAY_MUSIC.packageName)
        context.sendBroadcast(intent)
    }

    companion object {
        private const val TAG = "GooglePlayMusic"
    }
}