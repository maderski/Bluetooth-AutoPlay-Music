package maderski.bluetoothautoplaymusic.helpers

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MapApps.*
import maderski.bluetoothautoplaymusic.utils.PermissionUtils

/**
 * Created by Jason on 7/28/16.
 */
class PackageHelper(private val context: Context) {
    // Get all Installed Packages that are on the device
    val allInstalledPackages: List<ApplicationInfo> get() {
        val packageManager: PackageManager = context.packageManager
        return packageManager.getInstalledApplications(0).toList()
    }

    // Launches App that is associated with that package that was put into method
    fun launchPackage(packageName: String) {
        Log.d("Package intent: ", "$packageName started")
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            showUnableToLaunchToast(packageName)
        }
    }

    fun launchPackage(packageName: String, data: Uri, action: String) {
        Log.d("Package intent: ", "$packageName started")
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.action = action
            launchIntent.data = data
            context.startActivity(launchIntent)
        } else {
            showUnableToLaunchToast(packageName)
        }
    }

    private fun showUnableToLaunchToast(packageName: String) {
        val unableToLaunchMsg = context.getString(R.string.unable_to_launch)
        val toastMsg = when(packageName) {
            MAPS.packageName -> "$unableToLaunchMsg MAPS"
            WAZE.packageName -> "$unableToLaunchMsg WAZE"
            else -> "$unableToLaunchMsg Media Player"
        }
        Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
    }

    //Returns true if Package is on phone
    fun isPackageOnPhone(packageName: String): Boolean =
            allInstalledPackages.any { packageInfo -> packageInfo.packageName == packageName }

    // Set of Mediaplayers that is installed on the phone
    fun installedMediaPlayersSet(): Set<String> {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val actionMediaButtonReceivers = context.packageManager.queryBroadcastReceivers(intent, 0)
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
        var mapAppName = "Not Found"
        try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            val applicationLabel = context.packageManager.getApplicationLabel(appInfo).toString()
            mapAppName = if (applicationLabel.equals("MAPS", ignoreCase = true)) {
                "WAZE"
            } else {
                "GOOGLE MAPS"
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            return mapAppName
        }
    }

    // Is app running on phone
    fun isAppRunning(packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = activityManager.runningAppProcesses
        return processInfos.any { processInfo-> processInfo.processName == packageName }
    }

    fun getCurrentForegroundPackageName(): String {
        val hasUsageStatsPermission = PermissionUtils.hasUsageStatsPermission(context)
        if (hasUsageStatsPermission) {
            val usageStatsManager = context.getSystemService(Service.USAGE_STATS_SERVICE) as UsageStatsManager
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
            //TODO:// handle the case when usageStats permission is not granted
        }
        return PACKAGE_NOT_FOUND
    }

    fun sendBroadcast(intent: Intent) = context.sendBroadcast(intent)

    enum class MediaPlayers(val packageName: String) {
        GOOGLE_PLAY_MUSIC("com.google.android.music"),
        SPOTIFY("com.spotify.music"),
        PANDORA("com.pandora.android"),
        BEYOND_POD("mobi.beyondpod"),
        APPLE_MUSIC("com.apple.android.music"),
        FM_INDIA("com.fmindia.activities"),
        POWER_AMP("com.maxmpz.audioplayer"),
        DOUBLE_TWIST("com.doubleTwist.androidPlayer"),
        LISTEN_AUDIO_BOOK("com.acmeandroid.listen"),
        GOOGLE_PODCASTS("com.google.android.apps.podcasts"),
        DEEZERMUSIC("deezer.android.app"),
        POCKET_CASTS("au.com.shiftyjelly.pocketcasts"),
        RADIO_PARADISE("com.earthflare.android.radioparadisewidget.gpv2"),
        TUNE_IN_RADIO_PRO("radiotime.player"),
        FOOBAR_2000("com.foobar2000.foobar2000"),
        VANILLA_MUSIC("ch.blinkenlights.android.vanilla"),
        JIO_MUSIC("com.jio.media.jiobeats")
    }

    enum class MapApps(val packageName: String) {
        MAPS("com.google.android.apps.maps"),
        WAZE("com.waze")
    }

    companion object {
        private const val TAG = "PackageHelper"

        const val PACKAGE_NOT_FOUND = "packageNotFound"
    }
}
