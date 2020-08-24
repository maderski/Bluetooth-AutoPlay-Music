package maderski.bluetoothautoplaymusic.maps

import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper

interface MapLaunchStateDeterminer {
    fun getMapsLaunchState(packageName: String, isMapsRunning: Boolean): MapsLaunchState
}

class MapLaunchStateDeterminerImpl(
        private val preferencesHelper: PreferencesHelper
) : MapLaunchStateDeterminer {
    override fun getMapsLaunchState(packageName: String, isMapsRunning: Boolean): MapsLaunchState {
        val canLaunchDrivingMode = preferencesHelper.canLaunchDrivingMode
        val canLaunchDirections = preferencesHelper.canLaunchDirections
        val isUsingTimesToLaunch = preferencesHelper.isUsingTimesToLaunch

        return when {
            isMapsRunning -> MapsLaunchState.MAPS_IS_ALREADY_RUNNING
            canLaunchDrivingMode
                    && !canLaunchDirections
                    && !isUsingTimesToLaunch -> MapsLaunchState.DRIVING_MODE_MAPS_LAUNCH
            isUsingTimesToLaunch
                    && !canLaunchDirections
                    && !canLaunchDrivingMode -> MapsLaunchState.USE_TIMES_MAPS_LAUNCH
            canLaunchDrivingMode
                    && isUsingTimesToLaunch -> MapsLaunchState.DRIVING_MODE_AND_USE_TIMES_MAPS_LAUNCH
            canLaunchDirections
                    && isUsingTimesToLaunch -> MapsLaunchState.DIRECTIONS_AND_TIMES_MAPS_LAUNCH
            canLaunchDrivingMode
                    && canLaunchDirections
                    && isUsingTimesToLaunch -> MapsLaunchState.EVERYTHING_ENABLED_MAPS_LAUNCH
            else -> MapsLaunchState.REGULAR_MAPS_LAUNCH
        }
    }
}