package maderski.bluetoothautoplaymusic.di

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetoothactions.BTDisconnectActions
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.KeyEventControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.controls.mediaplayer.PlayAttempter
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.helpers.*
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMSharedPrefsAccess
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object KoinModules {
    val list = listOf(
            prefsModules,
            controlModules,
            firebaseModule,
            serviceModule,
            helperModules,
            notificationModule,
            permissionModule,
            btActionsModules
    )
}

val prefsModules = module {
    single { BAPMPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMPreferences.MY_PREFS_NAME)) }
    single { BAPMDataPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMDataPreferences.MY_PREFS_NAME)) }
}

val controlModules = module {
    single { ScreenONLock(get()) }
    single { KeyEventControl(androidContext()) }
    single { VolumeControl(androidContext()) }
    single { RingerControl(androidContext()) }
    single { PlayAttempter() }
    single { MediaPlayerControlManager(androidContext(), get(), get(), get(), get(), get())}
}

val firebaseModule = module {
    single { FirebaseHelper(androidContext()) }
}

val serviceModule = module {
    single { ServiceManager(androidContext()) }
}

val helperModules = module {
    single { PackageHelper(androidContext()) }
    single { TelephoneHelper(androidContext()) }
    single { LaunchAppHelper(androidContext(), get(), get()) }
    single { MediaSessionTokenHelper(androidContext()) }
}

val notificationModule = module {
    single { BAPMNotification(androidContext()) }
}

val permissionModule = module {
    single { BAPMPermissionHelper() }
}

val btActionsModules = module {
    single { BTDisconnectActions(androidContext()) }
}

