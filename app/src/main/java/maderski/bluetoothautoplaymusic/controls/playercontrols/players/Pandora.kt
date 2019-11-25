package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls
import maderski.bluetoothautoplaymusic.helpers.enums.MediaPlayers.*

class Pandora(context: Context) : PlayerControls(context) {

    override fun play() {
        Log.d(TAG, "Play Music")
        playMediaButton(PANDORA.packageName)
        playKeyEvent()
    }

    companion object {
        private const val TAG = "Pandora"
    }
}