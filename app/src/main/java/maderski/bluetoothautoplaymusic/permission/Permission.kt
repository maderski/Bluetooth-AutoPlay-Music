package maderski.bluetoothautoplaymusic.permission

import android.Manifest

enum class Permission(val value: String) {
    COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
    GET_USAGE_STATS("android:get_usage_stats"),
    ACCESS_NOTIFICATION_POLICY(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
}