package maderski.bluetoothautoplaymusic.controls.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageItemInfo
import android.content.pm.PackageManager
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import maderski.bluetoothautoplaymusic.services.BAPMNotificationListenerService

class MediaBrowserManager(
        context: Context,
        packageName: String,
        private val callBack: Callback
) : MediaBrowserCompat.ConnectionCallback() {
    interface Callback {
        fun onConnected(token: MediaSessionCompat.Token)
    }

    private val mediaBrowser: MediaBrowserCompat

    init {
        val packageManager = context.packageManager
        val pkgItemInfo = getPkgItemInfo(packageName, packageManager)
        if (pkgItemInfo != null) {
            val componentName = ComponentName(context, BAPMNotificationListenerService::class.java)
            mediaBrowser = MediaBrowserCompat(context, componentName, this, null)
        } else {
            throw IllegalStateException("Package Item Info is NULL!")
        }
    }

    fun connect() {
        with (mediaBrowser) {
            if (isConnected) {
                disconnect()
            }
            connect()
        }
    }

    override fun onConnected() {
        Log.d(TAG, "MEDIA BROWSER SERVICE CONNECTED!")
        mediaBrowser.run {
            callBack.onConnected(sessionToken)
            if (isConnected) {
                disconnect()
            }
        }
    }

    override fun onConnectionFailed() {
        super.onConnectionFailed()
        Log.d(TAG, "MEDIA BROWSER SERVICE CONNECTION FAILED!")
    }

    private fun getPkgItemInfo(packageName: String, packageManager: PackageManager): PackageItemInfo? {
        return try {
            val mediaBrowserIntent = Intent(MediaBrowserService.SERVICE_INTERFACE)
            mediaBrowserIntent.setPackage(packageName)
            // Build an Intent that only has the MediaBrowserService action and query
            // the PackageManager for apps that have services registered that can
            // receive it.
            val services = packageManager.queryIntentServices(
                    mediaBrowserIntent,
                    PackageManager.GET_RESOLVED_FILTER
            )
            services?.firstOrNull()?.serviceInfo
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Unable to load package details", e)
            null
        }
    }

    companion object {
        const val TAG = "MediaBrowserManager"
    }
}