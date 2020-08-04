package maderski.bluetoothautoplaymusic.detector.foreground

import android.app.usage.UsageEvents
import android.os.Build
import androidx.annotation.RequiresApi
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

@RequiresApi(api = Build.VERSION_CODES.Q)
class ForegroundDetectorAPI29Plus(
        private val systemServicesWrapper: SystemServicesWrapper
) : ForegroundDetector {
    override fun getForegroundedApp(): String? {
        val usageStatsManager = systemServicesWrapper.usageStats
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 1000 * 3600
        val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
        val eventOut = UsageEvents.Event()

        var packageName: String? = null
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(eventOut)
            if (eventOut.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                packageName = eventOut.packageName
                break
            }
        }
        return packageName
    }
}