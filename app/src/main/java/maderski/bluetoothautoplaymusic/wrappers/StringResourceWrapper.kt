package maderski.bluetoothautoplaymusic.wrappers

import android.content.Context
import maderski.bluetoothautoplaymusic.R

class StringResourceWrapper(context: Context) {
    val unableToLaunch = context.getString(R.string.unable_to_launch)
    val unableToLaunchMaps = context.getString(R.string.unable_to_launch_maps)
    val unableToLaunchWaze = context.getString(R.string.unable_to_launch_waze)
    val unableToLaunchMediaPlayer = context.getString(R.string.unable_to_launch_media_player)
    val noBluetoothDevice = context.getString(R.string.no_bluetooth_device)
    val wifiTurnedOn = context.getString(R.string.wifi_turned_on)
    val wifiTurnedOff = context.getString(R.string.wifi_turned_off)
    val newPermissionReqMsg = context.getString(R.string.new_permissions_required)
}