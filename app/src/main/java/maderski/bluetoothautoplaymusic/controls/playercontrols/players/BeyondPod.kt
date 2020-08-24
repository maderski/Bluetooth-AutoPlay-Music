package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class BeyondPod(
        private val context: Context,
        systemServicesWrapper: SystemServicesWrapper
) : PlayerControls(systemServicesWrapper) {
    override fun play() {
        Log.d(TAG, "Play Music")
        val intent = Intent()
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.action = "mobi.beyondpod.command.PLAY"
        context.sendBroadcast(intent)
    }

    companion object {
        private const val TAG = "BeyondPod"
    }
}