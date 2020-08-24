package maderski.bluetoothautoplaymusic.analytics.constants

import androidx.annotation.StringDef



object SelectionConstants {
    @StringDef(
        ABOUT,
        BRIGHT_TIME,
        DIM_TIME,
        MAPS_WAZE_SELECTOR,
        OPTIONS,
        RATE_ME,
        SET_AUTOPLAY_ONLY,
        BLUETOOTH_DEVICE,
        HEADPHONE_DEVICE,
        SET_WIFI_OFF_DEVICE,
        MORNING_START_TIME,
        MORNING_END_TIME,
        EVENING_START_TIME,
        EVENING_END_TIME,
        CUSTOM_START_TIME,
        CUSTOM_END_TIME,
        LAUNCH_WAZE_DIRECTIONS
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Selection

    const val SET_AUTOPLAY_ONLY = "set_autoplay_only"
    const val MAPS_WAZE_SELECTOR = "maps_waze_selector"
    const val OPTIONS = "option"
    const val RATE_ME = "rate_me"
    const val ABOUT = "about"
    const val BRIGHT_TIME = "bright_time"
    const val DIM_TIME = "dim_time"
    const val BLUETOOTH_DEVICE = "bluetooth_device"
    const val HEADPHONE_DEVICE = "headphone_device"
    const val SET_WIFI_OFF_DEVICE = "set_wifi_off_device"
    const val MORNING_START_TIME = "morning_start_time"
    const val MORNING_END_TIME = "morning_end_time"
    const val EVENING_START_TIME = "evening_start_time"
    const val EVENING_END_TIME = "evening_end_time"
    const val CUSTOM_START_TIME = "custom_start_time"
    const val CUSTOM_END_TIME = "custom_end_time"
    const val LAUNCH_WAZE_DIRECTIONS = "launch_waze_directions"
}