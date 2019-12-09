package maderski.bluetoothautoplaymusic.di

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTDisconnectActions
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTHeadphonesActions
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.KeyEventControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.controls.playattempters.BasicPlayAttempter
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock
import maderski.bluetoothautoplaymusic.helpers.*
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.bluetooth.receivers.BTConnectionReceiver
import maderski.bluetoothautoplaymusic.bluetooth.receivers.BTStateChangedReceiver
import maderski.bluetoothautoplaymusic.receivers.PowerConnectionReceiver
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
            btActionsModule
    )
}

val prefsModules = module {
    single { BAPMPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMPreferences.MY_PREFS_NAME)) }
    single { BAPMDataPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMDataPreferences.MY_PREFS_NAME)) }
}

val controlModules = module {
    single { ScreenONLock(get()) }
    single { KeyEventControl(androidContext()) }
    single { VolumeControl(get(), get()) }
    single { RingerControl(get(), get()) }
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
    single { PowerConnectionReceiver() }
}

val helperModules = module {
    single { PackageHelper(androidContext()) }
    single { PowerHelper(androidContext()) }
    single { TelephoneHelper(get(), get(), get(), get()) }
    single { LaunchAppHelper(androidContext(), get(), get()) }
    single { MediaSessionTokenHelper(androidContext()) }
    single { BluetoothConnectHelper() }
    single { PowerConnectedHelper(androidContext(), get(), get(), get()) }
    single { PreferencesHelper(get(), get()) }
    single { AndroidSystemServicesHelper(androidContext()) }
    single { HeadphonesConnectHelper() }
}

val notificationModule = module {
    single { BAPMNotification(androidContext(), get(), get()) }
}

val permissionModule = module {
    single { BAPMPermissionHelper() }
}

val btActionsModule = module {
    single { BTDisconnectActions(androidContext()) }
    single { BTConnectActions(androidContext()) }
    single { BTHeadphonesActions(androidContext()) }
}

