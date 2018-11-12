package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls

internal class Pandora(context: Context) : PlayerControls(context) {

    override fun play() {
        playMediaButton(PackageHelper.PANDORA)
        playKeyEvent()
    }

    companion object {
        private const val TAG = "Pandora"
    }
}