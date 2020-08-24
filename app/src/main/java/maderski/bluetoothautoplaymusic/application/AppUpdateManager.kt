package maderski.bluetoothautoplaymusic.application

import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.wrappers.PackageManagerWrapper
import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper

class AppUpdateManager(
        private val preferences: BAPMPreferences,
        private val packageManagerWrapper: PackageManagerWrapper,
        private val bapmNotification: BAPMNotification,
        private val stringResWrapper: StringResourceWrapper
) {
    fun updateWorkCheck(isShowingNotification: Boolean) {
        val lastUpdatedVersion = preferences.getLastAppVersion()
        val currentVersion = packageManagerWrapper.getBAPMVersionCode()
        if (lastUpdatedVersion != currentVersion) {
            preferences.setLastAppVersion(currentVersion)
            if (isShowingNotification) {
                bapmNotification.launchBAPMNotification(stringResWrapper.newPermissionReqMsg)
            }
        }
    }
}