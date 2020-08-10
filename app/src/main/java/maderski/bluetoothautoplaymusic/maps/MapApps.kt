package maderski.bluetoothautoplaymusic.maps

enum class MapApps(val packageName: String, val uiDisplayName: String, val applicationLabel: String) {
    MAPS("com.google.android.apps.maps", "GOOGLE MAPS", "MAPS"),
    WAZE("com.waze", "WAZE", "WAZE");

    companion object {
        fun getMapAppFrom(packageName: String) = when(packageName) {
            MAPS.packageName -> MAPS
            WAZE.packageName -> WAZE
            else -> MAPS // Return Maps if package name is not found
        }

        fun isMapApp(packageName: String) =
                packageName == MAPS.packageName || packageName == WAZE.packageName
    }
}