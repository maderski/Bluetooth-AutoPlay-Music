package maderski.bluetoothautoplaymusic.helpers

import android.app.usage.UsageEvents
import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.MAPS
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.WAZE
import maderski.bluetoothautoplaymusic.helpers.enums.MediaPlayers
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import maderski.bluetoothautoplaymusic.wrappers.PackageManagerWrapper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 7/28/16.
 */
class PackageHelper(
        private val systemServicesWrapper: SystemServicesWrapper,
        private val packageManagerWrapper: PackageManagerWrapper,
        private val permissionManager: PermissionManager
) {
    // Launches App that is associated with that package that was put into method
    fun getLaunchIntent(packageName: String): Intent? {
        Log.d("Package intent: ", "$packageName started")
        return packageManagerWrapper.getLaunchIntentForPackage(packageName)

    }

    //Returns true if Package is on phone
    fun isPackageOnPhone(packageName: String): Boolean =
            packageManagerWrapper.getAllInstalledPackages()
                    .any { packageInfo ->
                        packageInfo.packageName == packageName
                    }

    // Set of Mediaplayers that is installed on the phone
    fun installedMediaPlayersSet(): Set<String> {
        val actionMediaButtonReceivers = packageManagerWrapper.getActionMediaButtonReceivers()
        val installedMediaPlayers = actionMediaButtonReceivers.filter { resolveInfo ->
            val resolveInfoString = resolveInfo.toString()
            resolveInfoString.contains(".playback")
                    || resolveInfoString.contains("music")
                    || resolveInfoString.contains("Music")
                    || resolveInfoString.contains("audioplayer")
        }.map { musicPlayerResolveInfo ->
            val musicPlayerRIString = musicPlayerResolveInfo.toString()
            val resolveInfoSplit = musicPlayerRIString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            resolveInfoSplit[1].substring(0, resolveInfoSplit[1].indexOf("/"))
        }.toMutableSet()

        val mediaPlayerPackagesToAdd = MediaPlayers.values().map { it.packageName }
        val mediaPlayersInstalled = mediaPlayerPackagesToAdd.filter { packageName ->
            isPackageOnPhone(packageName)
        }
        installedMediaPlayers.addAll(mediaPlayersInstalled)

        return installedMediaPlayers
    }

    // Returns Map App Name, intentionally only works with Google maps and Waze
    fun getMapAppName(packageName: String): String {
        return try {
            val applicationLabel = packageManagerWrapper.getApplicationLabel(packageName)
            if (applicationLabel.equals(MAPS.applicationLabel, ignoreCase = true)) {
                WAZE.uiDisplayName
            } else {
                MAPS.uiDisplayName
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Not Found"
        }
    }

    fun getCurrentForegroundPackageName(): String {
        val hasUsageStatsPermission = permissionManager.hasUsageStatsPermission()
        if (hasUsageStatsPermission) {
            val usageStatsManager = systemServicesWrapper.usageStats
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 1000 * 3600
            val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
            val eventOut = UsageEvents.Event()

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(eventOut)
                return if (eventOut.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    eventOut.packageName
                } else {
                    PACKAGE_NOT_FOUND
                }
            }
        } else {
            //TODO: handle the case when usageStats permission is not granted
        }
        return PACKAGE_NOT_FOUND
    }

    fun getActivityManager() = systemServicesWrapper.activityManager

    companion object {
        private const val TAG = "PackageHelper"

        const val PACKAGE_NOT_FOUND = "packageNotFound"

    }
}
