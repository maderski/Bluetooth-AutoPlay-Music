package maderski.bluetoothautoplaymusic.di

import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMSharedPrefsAccess
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object KoinModules {
    val list = listOf(
            prefsModules,
            controlModules
    )
}

val prefsModules = module {
    single { BAPMPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMPreferences.MY_PREFS_NAME)) }
    single { BAPMDataPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMDataPreferences.MY_PREFS_NAME)) }
}

val controlModules = module {
    single { ScreenONLock(get()) }
}

