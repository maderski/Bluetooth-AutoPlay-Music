package maderski.bluetoothautoplaymusic.application

import android.app.Application
import maderski.bluetoothautoplaymusic.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BAPMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BAPMApplication)
            modules(KoinModules.list)
        }
    }
}