package maderski.bluetoothautoplaymusic.analytics.constants

import android.support.annotation.StringDef

object BTActionsLaunchConstants {
    @StringDef(
        TELEPHONE,
        POWER,
        BLUETOOTH
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class BTActionsLaunch

    const val TELEPHONE = "off_telephone_launch"
    const val POWER = "power_plugged_in_launch"
    const val BLUETOOTH = "bluetooth_connected_launch"
}