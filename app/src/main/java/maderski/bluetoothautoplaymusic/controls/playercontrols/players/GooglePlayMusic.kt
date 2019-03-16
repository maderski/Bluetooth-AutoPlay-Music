package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls

class GooglePlayMusic(context: Context) : PlayerControls(context) {

    override fun play() {
        Log.d(TAG, "Play Music")
        val intent = Intent("com.android.music.musicservicecommand")
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra("command", "play")
        intent.setPackage(PackageHelper.GOOGLEPLAYMUSIC)
        context.sendBroadcast(intent)
    }

    companion object {
        private const val TAG = "GooglePlayMusic"
    }
}