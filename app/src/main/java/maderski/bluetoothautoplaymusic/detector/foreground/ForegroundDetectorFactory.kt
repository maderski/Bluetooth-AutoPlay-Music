package maderski.bluetoothautoplaymusic.detector.foreground

import android.os.Build
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

class ForegroundDetectorFactory(private val systemServicesWrapper: SystemServicesWrapper) {
    fun getForegroundDetector(): ForegroundDetector = when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> ForegroundDetectorAPI29Plus(systemServicesWrapper)
        else -> ForegroundDetectorAPI28Below(systemServicesWrapper)
    }
}