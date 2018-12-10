package maderski.bluetoothautoplaymusic.helpers

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.support.annotation.StringDef
import android.util.Log
import android.widget.Toast
import java.util.*

/**
 * Created by Jason on 7/28/16.
 */
open class PackageHelper {
    // Launches App that is associated with that package that was put into method
    fun launchPackage(context: Context, pkg: String) {
        Log.d("Package intent: ", "$pkg started")
        val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            val toastMsg = if (pkg == MAPS || pkg == WAZE)
                "Unable to launch MAPS or WAZE"
            else
                "Unable to launch Music player"
            Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
        }
    }

    fun launchPackage(context: Context, packageName: String, data: Uri, action: String) {
        Log.d("Package intent: ", "$packageName started")
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.action = action
            launchIntent.data = data
            context.startActivity(launchIntent)
        } else {
            val toastMsg = if (packageName == MAPS || packageName == WAZE)
                "Unable to launch MAPS or WAZE"
            else
                "Unable to launch Music player"
            Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
        }
    }

    //Returns true if Package is on phone
    fun checkPkgOnPhone(context: Context, pkg: String): Boolean {
        val packages: List<ApplicationInfo>
        val pm: PackageManager

        pm = context.packageManager
        packages = pm.getInstalledApplications(0)
        for (packageInfo in packages) {
            if (packageInfo.packageName == pkg)
                return true
        }
        return false
    }

    //List of Mediaplayers that is installed on the phone
    fun listOfInstalledMediaPlayers(context: Context): List<String> {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val pkgAppsList = context.packageManager.queryBroadcastReceivers(intent, 0)
        val installedMediaPlayers = ArrayList<String>()

        for (ri in pkgAppsList) {
            val resolveInfo = ri.toString()
            if (resolveInfo.contains(".playback")
                    || resolveInfo.contains("music")
                    || resolveInfo.contains("Music")
                    || resolveInfo.contains("audioplayer")
                    || resolveInfo.contains(BEYONDPOD)
                    || resolveInfo.contains(POCKET_CASTS)
                    || resolveInfo.contains(DEEZERMUSIC)
                    || resolveInfo.contains(DOUBLETWIST)
                    || resolveInfo.contains(LISTENAUDIOBOOK)) {
                val resolveInfoSplit = resolveInfo.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val pkg = resolveInfoSplit[1].substring(0, resolveInfoSplit[1].indexOf("/"))
                if (!installedMediaPlayers.contains(pkg)) {
                    installedMediaPlayers.add(pkg)
                }
            }
        }

        val packagesToCheck = arrayOf(
                GOOGLEPODCASTS,
                PANDORA,
                RADIOPARADISE,
                TUNEINRADIOPRO,
                FOOBAR2000,
                VANILLAMUSIC,
                JIOMUSIC
        )

        packagesToCheck.forEach { packageName ->
            val isPackageInstalled = checkPkgOnPhone(context, packageName)
            if (isPackageInstalled) {
                installedMediaPlayers.add(packageName)
            }
        }

        return installedMediaPlayers
    }

    // Returns Map App Name, intentionally only works with Google maps and Waze
    fun getMapAppName(context: Context, pkg: String): String {
        var mapAppName = "Not Found"
        try {
            val appInfo = context.packageManager.getApplicationInfo(pkg, 0)
            mapAppName = context.packageManager.getApplicationLabel(appInfo).toString()
            if (mapAppName.equals("MAPS", ignoreCase = true)) {
                mapAppName = "WAZE"
            } else {
                mapAppName = "GOOGLE MAPS"
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

        return mapAppName
    }

    // List of Installed Packages on the phone
    fun listOfPackagesOnPhone(context: Context) {
        val pm = context.packageManager
        val appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (pkg in appInfo) {
            Log.d(TAG, "Installed Pkg: " + pkg.packageName)
        }
    }

    // Is app running on phone
    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        activityManager?.let {
            val processInfos = it.runningAppProcesses

            for (processInfo in processInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }

        return false
    }

    companion object {
        private const val TAG = "PackageTools"

        // Package Names
        @StringDef(
                MAPS,
                WAZE,
                GOOGLEPLAYMUSIC,
                SPOTIFY,
                PANDORA,
                BEYONDPOD,
                APPLEMUSIC,
                FMINDIA,
                POWERAMP,
                DOUBLETWIST,
                LISTENAUDIOBOOK,
                GOOGLEPODCASTS,
                DEEZERMUSIC,
                POCKET_CASTS,
                RADIOPARADISE,
                TUNEINRADIOPRO,
                FOOBAR2000,
                VANILLAMUSIC,
                JIOMUSIC
        )
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class PackageName

        const val MAPS = "com.google.android.apps.maps"
        const val WAZE = "com.waze"
        const val GOOGLEPLAYMUSIC = "com.google.android.music"
        const val SPOTIFY = "com.spotify.music"
        const val PANDORA = "com.pandora.android"
        const val BEYONDPOD = "mobi.beyondpod"
        const val APPLEMUSIC = "com.apple.android.music"
        const val FMINDIA = "com.fmindia.activities"
        const val POWERAMP = "com.maxmpz.audioplayer"
        const val DOUBLETWIST = "com.doubleTwist.androidPlayer"
        const val LISTENAUDIOBOOK = "com.acmeandroid.listen"
        const val GOOGLEPODCASTS = "com.google.android.apps.podcasts"
        const val DEEZERMUSIC = "deezer.android.app"
        const val POCKET_CASTS = "au.com.shiftyjelly.pocketcasts"
        const val RADIOPARADISE = "com.earthflare.android.radioparadisewidget.gpv2"
        const val TUNEINRADIOPRO = "radiotime.player"
        const val FOOBAR2000 = "com.foobar2000.foobar2000"
        const val VANILLAMUSIC = "ch.blinkenlights.android.vanilla"
        const val JIOMUSIC = "com.jio.media.jiobeats"
    }
}
