package maderski.bluetoothautoplaymusic.wrappers

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

class PackageManagerWrapper (private val context: Context) {
    // Get all Installed Packages that are on the device
    fun getAllInstalledPackages(): List<ApplicationInfo> {
        val packageManager: PackageManager = context.packageManager
        return packageManager.getInstalledApplications(0).toList()
    }

    fun getLaunchIntentForPackage(packageName: String) =
            context.packageManager.getLaunchIntentForPackage(packageName)

    fun getActionMediaButtonReceivers(): MutableList<ResolveInfo> =
        context.packageManager.queryBroadcastReceivers(Intent(Intent.ACTION_MEDIA_BUTTON), 0)

    fun getApplicationInfo(packageName: String): ApplicationInfo =
            context.packageManager.getApplicationInfo(packageName, 0)

    fun getApplicationLabel(applicationInfo: ApplicationInfo) =
            context.packageManager.getApplicationLabel(applicationInfo).toString()

    fun getApplicationLabel(packageName: String) =
            context.packageManager.getApplicationLabel(getApplicationInfo(packageName)).toString()
}