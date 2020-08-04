package maderski.bluetoothautoplaymusic.detector.foreground

import android.app.usage.UsageEvents
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class ForegroundDetectorAPI28Below(
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
            if (eventOut.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                packageName = eventOut.packageName
                break
            }
        }
        return packageName
    }
}