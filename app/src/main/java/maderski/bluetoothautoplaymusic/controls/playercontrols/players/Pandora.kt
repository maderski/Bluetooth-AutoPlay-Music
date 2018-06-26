package maderski.bluetoothautoplaymusic.controls.playercontrols.players

import android.content.Context
import maderski.bluetoothautoplaymusic.PackageTools
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls

internal class Pandora(context: Context) : PlayerControls(context) {

    override fun play() {
        playMediaButton(PackageTools.PackageName.PANDORA)
        playKeyEvent()
    }

    companion object {
        private const val TAG = "Pandora"
    }
}