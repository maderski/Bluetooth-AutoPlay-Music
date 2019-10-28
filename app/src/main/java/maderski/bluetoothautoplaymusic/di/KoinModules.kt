package maderski.bluetoothautoplaymusic.di

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.btactions.BTDisconnectActions
import maderski.bluetoothautoplaymusic.btactions.BTHeadphonesActions
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.KeyEventControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.controls.playattempters.BasicPlayAttempter
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.helpers.*
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.receivers.BTConnectionReceiver
import maderski.bluetoothautoplaymusic.receivers.BTStateChangedReceiver
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
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
            receiverModule,
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
    single { BasicPlayAttempter() }
    factory { MediaPlayerControlManager(androidContext(), get(), get(), get(), get(), get())}
}

val firebaseModule = module {
    single { FirebaseHelper(androidContext()) }
}

val serviceModule = module {
    single { ServiceManager(androidContext()) }
}

val receiverModule = module {
    single { BTConnectionReceiver() }
    single { BTStateChangedReceiver() }
}

val helperModules = module {
    single { PackageHelper(androidContext()) }
    single { PowerHelper(androidContext()) }
    single { TelephoneHelper(androidContext(), get()) }
    single { LaunchAppHelper(androidContext(), get(), get()) }
    single { MediaSessionTokenHelper(androidContext()) }
    single { BluetoothConnectHelper() }
}

val notificationModule = module {
    single { BAPMNotification(androidContext()) }
}

val permissionModule = module {
    single { BAPMPermissionHelper() }
}

val btActionsModules = module {
    single { BTDisconnectActions(androidContext()) }
    single { BTConnectActions(androidContext()) }
    single { BTHeadphonesActions(androidContext()) }
}

