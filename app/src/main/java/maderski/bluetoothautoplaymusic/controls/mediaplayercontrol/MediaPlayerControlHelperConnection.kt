package maderski.bluetoothautoplaymusic.controls.mediaplayercontrol

import android.content.pm.PackageManager
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log

class MediaPlayerControlHelperConnection {

    fun getMediaPlayerTokens(
            controllers: Collection<MediaControllerCompat>,
            packageManager: PackageManager
    ): List<MediaPlayerToken> {
//        return controllers.map {
//            val token = it.sessionToken
//            if (token != null) {
//                MediaPlayerToken(it.packageName, token)
//            } else {
//                val mediaBrowserManager =
//            }
//        }
        return listOf()
    }

    private fun getAppInfo(packageName: String, packageManager: PackageManager): String? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            appInfo.name
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Unable to load package details", e)
            null
        }
    }

    companion object {
        const val TAG = "MediaPlayerControlHelpe"
    }
}