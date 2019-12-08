package maderski.bluetoothautoplaymusic.helpers

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager

class AndroidSystemServicesHelper (context: Context) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}