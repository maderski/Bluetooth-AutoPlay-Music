package maderski.bluetoothautoplaymusic.controls.mediaplayercontrol

import android.support.v4.media.session.MediaSessionCompat

data class MediaPlayerToken(
        val packageName: String,
        val sessionToken: MediaSessionCompat.Token
)