package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls
import maderski.bluetoothautoplaymusic.helpers.enums.MediaPlayers.*
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class Pandora(
        private val context: Context,
        systemServicesWrapper: SystemServicesWrapper
) : PlayerControls(systemServicesWrapper) {

    override fun play() {
        Log.d(TAG, "Play Music")
        playMediaButton(context, PANDORA.packageName)
        playKeyEvent()
    }

    companion object {
        private const val TAG = "Pandora"
    }
}