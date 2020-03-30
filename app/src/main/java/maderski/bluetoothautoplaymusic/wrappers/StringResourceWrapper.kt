package maderski.bluetoothautoplaymusic.wrappers

import android.content.Context
import maderski.bluetoothautoplaymusic.R

class StringResourceWrapper(context: Context) {
    val unableToLaunch = context.getString(R.string.unable_to_launch)
    val unableToLaunchMaps = context.getString(R.string.unable_to_launch_maps)
    val unableToLaunchWaze = context.getString(R.string.unable_to_launch_waze)
    val unableToLaunchMediaPlayer = context.getString(R.string.unable_to_launch_media_player)
}