package maderski.bluetoothautoplaymusic.wrappers

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.ClipboardManager
import android.content.Context
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.telephony.TelephonyManager

class AndroidSystemServicesWrapper(private val context: Context) {
    val usageStats = context.getSystemService(Service.USAGE_STATS_SERVICE) as UsageStatsManager
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}