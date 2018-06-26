package maderski.bluetoothautoplaymusic.controls.playercontrols

import android.content.Context
import maderski.bluetoothautoplaymusic.PackageTools
import maderski.bluetoothautoplaymusic.controls.playercontrols.players.*

object PlayerControlsFactory {
    @JvmStatic
    fun getPlayerControl(context: Context, selectedPlayerPkgName: String): PlayerControls =
        when(selectedPlayerPkgName) {
            PackageTools.PackageName.SPOTIFY -> Spotify(context)
            PackageTools.PackageName.BEYONDPOD -> BeyondPod(context)
            PackageTools.PackageName.FMINDIA -> FMIndia(context)
            PackageTools.PackageName.GOOGLEPLAYMUSIC -> GooglePlayMusic(context)
            PackageTools.PackageName.PANDORA -> Pandora(context)
            else -> OtherMusicPlayer(context)
        }
}