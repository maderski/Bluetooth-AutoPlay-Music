package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothDevice
import maderski.bluetoothautoplaymusic.maps.MapApps
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

class PreferencesHelper(
        private val preferences: BAPMPreferences
) {
    // BAPM Preferences
    val customLocationName get() = preferences.getCustomLocationName()
    val canLaunchDirections get() = preferences.getCanLaunchDirections()
    val canLaunchDrivingMode
        get() = preferences.getLaunchMapsDrivingMode() &&
                mapAppName == MapApps.MAPS.packageName
    val isUsingTimesToLaunch get() = preferences.getUseTimesToLaunchMaps()

    val morningStartTime get() = preferences.getMorningStartTime()
    val morningEndTime get() = preferences.getMorningEndTime()

    val eveningStartTime get() = preferences.getEveningStartTime()
    val eveningEndTime get() = preferences.getEveningEndTime()

    val customStartTime get() = preferences.getCustomStartTime()
    val customEndTime get() = preferences.getCustomEndTime()

    val musicPlayerPkgName get() = preferences.getPkgSelectedMusicPlayer()

    val daysToLaunchHome get() = preferences.getHomeDaysToLaunchMaps() ?: setOf<String>()
    val daysToLaunchWork get() = preferences.getWorkDaysToLaunchMaps() ?: setOf<String>()
    val daysToLaunchCustom get() = preferences.getCustomDaysToLaunchMaps() ?: setOf<String>()

    val waitTillOffPhone get() = preferences.getWaitTillOffPhone()
    val unlockScreen get() = preferences.getUnlockScreen()
    val mapAppName get() = preferences.getMapsChoice()
    val keepScreenON get() = preferences.getKeepScreenON()
    val volumeMAX get() = preferences.getMaxVolume()
    val canAutoPlayMusic get() = preferences.getAutoPlayMusic()
    val isLaunchingMusicPlayer get() = preferences.getLaunchMusicPlayer()
    val isLaunchingMaps get() = preferences.getLaunchGoogleMaps()
    val priorityMode get() = preferences.getPriorityMode()
    val shouldCloseWaze get() = preferences.getCloseWazeOnDisconnect()
    val originalVolume get() = preferences.getRestoreNotificationVolume()
    val waitTillPowerConnected = preferences.getPowerConnected()

    private val selectedBAPMDevices get() = preferences.getBAPMDevices()

    fun isASelectedBTDevice(bluetoothDevice: BluetoothDevice?): Boolean =
            if (bluetoothDevice == null) {
                false
            } else {
                selectedBAPMDevices.any {
                    it.name == bluetoothDevice.name && it.macAddress == bluetoothDevice.address
                }
            }

    fun getUserSetMaxVolume(deviceMaxVolume: Int): Int = preferences.getUserSetMaxVolume(deviceMaxVolume)
}