package maderski.bluetoothautoplaymusic.helpers

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.util.Log
import maderski.bluetoothautoplaymusic.services.BAPMNotificationListenerService
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class MediaSessionTokenHelper(
        private val context: Context,
        private val systemServicesWrapper: SystemServicesWrapper
) {
    fun getMediaSessionToken(packageName: String): MediaSession.Token? {

        val componentName = ComponentName(context, BAPMNotificationListenerService::class.java)
        val mediaSessionManager = systemServicesWrapper.mediaSessionManager
        val activeSessions = mediaSessionManager.getActiveSessions(componentName)
        activeSessions.forEach {
            Log.d(TAG, "MediaController FOUND: ${it.packageName}")
        }
        val matchingSession = activeSessions.find {
            it.packageName == packageName
        }
        return matchingSession?.sessionToken
    }

    companion object {
        const val TAG = "MediaSessionTokenHelper"
    }
}