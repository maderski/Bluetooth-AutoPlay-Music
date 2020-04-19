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
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControlsFactory
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.launchers.MapAppLauncher
import maderski.bluetoothautoplaymusic.receivers.PowerConnectionReceiver
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMSharedPrefsAccess
import maderski.bluetoothautoplaymusic.wrappers.PackageManagerWrapper
import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper
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
            btActionsModule,
            launcherModule,
            wrapperModule
    )
}

val prefsModules = module {
    single { BAPMPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMPreferences.MY_PREFS_NAME)) }
    single { BAPMDataPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMDataPreferences.MY_PREFS_NAME)) }
}

val controlModules = module {
    single { ScreenONLock(get()) }
    single { KeyEventControl(androidContext(), get()) }
    single { VolumeControl(get(), get()) }
    single { RingerControl(get(), get()) }
    single { BasicPlayAttempter() }
    factory { MediaPlayerControlManager(
            androidContext(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
    ) }
}

val firebaseModule = module {
    single { FirebaseHelper(androidContext()) }
}

val serviceModule = module {
    single { ServiceManager(androidContext(), get()) }
}

val receiverModule = module {
    single { BTConnectionReceiver() }
    single { BTStateChangedReceiver() }
    single { PowerConnectionReceiver() }
}

val helperModules = module {
    single { PackageHelper(get(), get()) }
    single { PowerHelper(androidContext()) }
    single { TelephoneHelper(get(), get(), get(), get()) }
    single { LaunchHelper(androidContext(), get(), get()) }
    single { MediaSessionTokenHelper(androidContext()) }
    single { BluetoothConnectHelper() }
    single { PowerConnectedHelper(get(), get(), get(), get()) }
    single { PreferencesHelper(get(), get()) }
    single { HeadphonesConnectHelper() }
    single { ToastHelper(androidContext()) }
    single { BluetoothDeviceHelper(get(), get()) }
}

val notificationModule = module {
    single { BAPMNotification(androidContext(), get(), get()) }
}

val permissionModule = module {
    single { BAPMPermissionHelper() }
}

val btActionsModule = module {
    single {
        BTDisconnectActions(
                androidContext(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
        )
    }
    single {
        BTConnectActions(
                androidContext(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
        )
    }
    single { BTHeadphonesActions(androidContext()) }
}

val launcherModule = module {
    single { MapAppLauncher(get(), get()) }
}

val wrapperModule = module {
    factory { SystemServicesWrapper(androidContext()) }
    factory { PackageManagerWrapper(androidContext()) }
    factory { StringResourceWrapper(androidContext()) }
}

val factoryModule = module {
    single { PlayerControlsFactory(androidContext(), get()) }
}

