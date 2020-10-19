package maderski.bluetoothautoplaymusic.controls.playercontrols

import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.playercontrols.players.*
import maderski.bluetoothautoplaymusic.helpers.enums.MediaPlayers.*
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class PlayerControlsFactory(
        private val context: Context,
        private val systemServicesWrapper: SystemServicesWrapper
) {
    fun getPlayerControl(selectedPlayerPkgName: String): PlayerControls? =
            when (selectedPlayerPkgName) {
                SPOTIFY.packageName -> Spotify(context, systemServicesWrapper)
                BEYOND_POD.packageName -> BeyondPod(context, systemServicesWrapper)
                FM_INDIA.packageName -> FMIndia(context, systemServicesWrapper, selectedPlayerPkgName)
                PANDORA.packageName -> Pandora(context, systemServicesWrapper)
                else -> {
                    Log.d(TAG, "APP does not have player controls setup")
                    null
                }
            }

    companion object {
        private const val TAG = "PlayerControlsFactory"
    }
}