package maderski.bluetoothautoplaymusic.controls.mediaplayercontrol

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat

class MediaBrowserManager(
        packageName: String,
        infoName: String
): MediaBrowserCompat.ConnectionCallback() {
//    @Inject
    lateinit var context: Context

    private val mediaBrowser: MediaBrowserCompat

    init {
        val componentName = ComponentName(packageName, infoName)
        mediaBrowser = MediaBrowserCompat(context, componentName, this, null)
    }

    fun connect() {
        mediaBrowser.connect()
    }

    fun disconnect() {
        if (mediaBrowser.isConnected) {
            mediaBrowser.disconnect()
        }
    }

    override fun onConnected() {
       mediaBrowser.run {

       }
    }
}