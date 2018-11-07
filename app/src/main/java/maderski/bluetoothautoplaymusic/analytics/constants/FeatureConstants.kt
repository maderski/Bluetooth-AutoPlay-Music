package maderski.bluetoothautoplaymusic.analytics.constants

import android.support.annotation.StringDef

object FeatureConstants {
    @StringDef(
            LAUNCH_MAPS,
            KEEP_SCREEN_ON,
            PRIORITY_MODE,
            MAX_VOLUME,
            LAUNCH_MAPS,
            LAUNCH_MUSIC_PLAYER,
            DISMISS_KEYGUARD
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Feature

    const val LAUNCH_MAPS = "launch_maps"
    const val KEEP_SCREEN_ON = "keep_screen_on"
    const val PRIORITY_MODE = "priority_mode"
    const val MAX_VOLUME = "max_volume"
    const val LAUNCH_MUSIC_PLAYER = "launch_music_player"
    const val DISMISS_KEYGUARD = "dismiss_keyguard"
}