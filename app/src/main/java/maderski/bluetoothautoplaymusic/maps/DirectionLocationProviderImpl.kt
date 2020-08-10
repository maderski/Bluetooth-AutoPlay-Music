package maderski.bluetoothautoplaymusic.maps

import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import java.util.*

interface DirectionLocationProvider {
    fun getLocationName(directionLocation: DirectionLocation): String
    fun getDirectionLocationForToday(): DirectionLocation
}

class DirectionLocationProviderImpl(
        private val preferencesHelper: PreferencesHelper
) : DirectionLocationProvider {
    override fun getLocationName(directionLocation: DirectionLocation): String =
            if (directionLocation == DirectionLocation.CUSTOM) {
                preferencesHelper.customLocationName
            } else {
                directionLocation.location
            }

    override fun getDirectionLocationForToday(): DirectionLocation =
            getDirectionLocations().find { directionLocation ->
                canLaunchOnThisDay(directionLocation)
            } ?: DirectionLocation.NONE

    private fun canLaunchOnThisDay(directionLocation: DirectionLocation): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK).toString()

        val daysToLaunchHome = preferencesHelper.daysToLaunchHome
        val daysToLaunchWork = preferencesHelper.daysToLaunchWork
        val daysToLaunchCustom = preferencesHelper.daysToLaunchCustom

        val canLaunch = when (directionLocation) {
            DirectionLocation.HOME -> daysToLaunchHome.contains(today)
            DirectionLocation.WORK -> daysToLaunchWork.contains(today)
            DirectionLocation.CUSTOM -> daysToLaunchCustom.contains(today)
            DirectionLocation.NONE -> false
        }

        Log.d(TAG, "Day of the week: $today")
        Log.d(TAG, "Can Launch: $canLaunch")
        Log.d(TAG, "Direction Location: $directionLocation")

        return canLaunch
    }

    private fun getDirectionLocations(): List<DirectionLocation> {
        val canLaunchWork = canLaunchDuringMorningTimes()
        val canLaunchHome = canLaunchDuringEveningTimes()
        val canLaunchCustom = canLaunchDuringCustomTimes()
        val directionLocations = mutableListOf<DirectionLocation>()
        when {
            canLaunchWork -> directionLocations.add(DirectionLocation.WORK)
            canLaunchHome -> directionLocations.add(DirectionLocation.HOME)
            canLaunchCustom -> directionLocations.add(DirectionLocation.CUSTOM)
        }
        return directionLocations
    }

    private fun canLaunchDuringMorningTimes(): Boolean {
        val morningStartTime = preferencesHelper.morningStartTime
        val morningEndTime = preferencesHelper.morningEndTime
        return isWithinTimeSpan(morningStartTime, morningEndTime)
    }

    private fun canLaunchDuringEveningTimes(): Boolean {
        val eveningStartTime = preferencesHelper.eveningStartTime
        val eveningEndTime = preferencesHelper.eveningEndTime
        return isWithinTimeSpan(eveningStartTime, eveningEndTime)
    }

    private fun canLaunchDuringCustomTimes(): Boolean {
        val customStartTime = preferencesHelper.customStartTime
        val customEndTime = preferencesHelper.customEndTime
        return isWithinTimeSpan(customStartTime, customEndTime)
    }

    private fun isWithinTimeSpan(startTime: Int, endTime: Int): Boolean {
        val current24hrTime = TimeHelper.current24hrTime
        val timeHelper = TimeHelper(startTime, endTime, current24hrTime)
        return timeHelper.isWithinTimeSpan
    }

    companion object {
        private const val TAG = "DirectionLocationProviderImpl"
    }
}