package maderski.bluetoothautoplaymusic.analytics.constants

import android.support.annotation.StringDef

object OptionConstants {
    @StringDef(
            AUTO_BRIGHTNESS,
            CALL_COMPLETED,
            GO_HOME,
            PLAY_MUSIC,
            POWER_REQUIRED,
            MAX_VOLUME_SET,
            HEADPHONE_VOLUME_SET,
            SHOW_NOTIFICATION,
            WIFI_OFF_USE_TIME_SPANS,
            PRIORITY_MODE
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Option

    const val PLAY_MUSIC = "play_music"
    const val POWER_REQUIRED = "power_required"
    const val GO_HOME = "go_home"
    const val CALL_COMPLETED = "call_completed"
    const val AUTO_BRIGHTNESS = "auto_brightness"
    const val MAX_VOLUME_SET = "max_volume_set"
    const val HEADPHONE_VOLUME_SET = "headphone_volume_set"
    const val SHOW_NOTIFICATION = "show_notification"
    const val WIFI_OFF_USE_TIME_SPANS = "wifi_off_use_time_spans"
    const val PRIORITY_MODE = "priority_mode"
}