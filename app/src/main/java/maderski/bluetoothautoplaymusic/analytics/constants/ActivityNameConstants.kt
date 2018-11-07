package maderski.bluetoothautoplaymusic.analytics.constants

import android.support.annotation.StringDef

object ActivityNameConstants {
    @StringDef(
        MAIN,
        SETTINGS,
        LAUNCH_BAPM
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class ActivityName

    const val MAIN = "main_activity"
    const val SETTINGS = "settings_activity"
    const val LAUNCH_BAPM = "dismiss_keyguard_activity"
}