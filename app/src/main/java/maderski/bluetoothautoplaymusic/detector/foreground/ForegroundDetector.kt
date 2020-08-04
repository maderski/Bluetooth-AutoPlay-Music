package maderski.bluetoothautoplaymusic.detector.foreground

interface ForegroundDetector {
    fun getForegroundedApp(): String?
}