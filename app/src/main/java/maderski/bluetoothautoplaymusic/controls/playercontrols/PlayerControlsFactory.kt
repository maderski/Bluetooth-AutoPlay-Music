package maderski.bluetoothautoplaymusic.controls.playercontrols

import android.content.Context
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.controls.playercontrols.players.*

object PlayerControlsFactory {
    @JvmStatic
    fun getPlayerControl(context: Context, selectedPlayerPkgName: String): PlayerControls =
        when(selectedPlayerPkgName) {
            PackageHelper.SPOTIFY -> Spotify(context)
            PackageHelper.BEYONDPOD -> BeyondPod(context)
            PackageHelper.FMINDIA -> FMIndia(context)
            PackageHelper.GOOGLEPLAYMUSIC -> GooglePlayMusic(context)
            PackageHelper.PANDORA -> Pandora(context)
            else -> OtherMusicPlayer(context)
        }
}