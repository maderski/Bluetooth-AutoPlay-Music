package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls

internal class Pandora(context: Context) : PlayerControls(context) {

    override fun play() {
        Log.d(TAG, "Play Music")
        playMediaButton(PackageHelper.PANDORA)
        playKeyEvent()
    }

    companion object {
        private const val TAG = "Pandora"
    }
}