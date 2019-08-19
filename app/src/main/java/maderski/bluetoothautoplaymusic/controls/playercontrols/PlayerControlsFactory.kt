package maderski.bluetoothautoplaymusic.controls.playercontrols

import android.content.Context
import maderski.bluetoothautoplaymusic.controls.playercontrols.players.*
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MediaPlayers.*

object PlayerControlsFactory {
    @JvmStatic
    fun getPlayerControl(context: Context, selectedPlayerPkgName: String): PlayerControls =
        when(selectedPlayerPkgName) {
            SPOTIFY.packageName -> Spotify(context)
            BEYOND_POD.packageName -> BeyondPod(context)
            FM_INDIA.packageName -> FMIndia(context, selectedPlayerPkgName)
            GOOGLE_PLAY_MUSIC.packageName -> GooglePlayMusic(context)
            PANDORA.packageName -> Pandora(context)
            else -> OtherMusicPlayer(context, selectedPlayerPkgName)
        }
}