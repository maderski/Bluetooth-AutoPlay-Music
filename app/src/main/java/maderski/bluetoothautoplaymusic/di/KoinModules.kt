package maderski.bluetoothautoplaymusic.di

import com.google.gson.Gson
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetooth.BTConnectionManager
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTDisconnectActions
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
import maderski.bluetoothautoplaymusic.controls.playattempters.CoroutinePlayAttempter
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControlsFactory
import maderski.bluetoothautoplaymusic.detector.foreground.ForegroundDetectorFactory
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.maps.MapLauncherFactory
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import maderski.bluetoothautoplaymusic.power.OnPowerConnectedAction
import maderski.bluetoothautoplaymusic.power.PowerInfo
import maderski.bluetoothautoplaymusic.receivers.PowerConnectionReceiver
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMSharedPrefsAccess
import maderski.bluetoothautoplaymusic.wrappers.PackageManagerWrapper
import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object KoinModules {
    val list = listOf(
            prefsModule,
            controlModule,
            serviceModule,
            receiverModule,
            helperModule,
            notificationModule,
            permissionModule,
            bluetoothModule,
            wrapperModule,
            factoryModule,
            thirdPartyModule,
            detectorModule,
            powerModule
    )
}

val thirdPartyModule = module {
    factory { Gson() }
}

val prefsModule = module {
    single { BAPMPreferences(BAPMSharedPrefsAccess(androidContext(), BAPMPreferences.MY_PREFS_NAME, get())) }
}

val controlModule = module {
    single { ScreenONLock(get(), get()) }
    single { KeyEventControl(androidContext(), get()) }
    single { VolumeControl(get(), get()) }
    single { RingerControl(get(), get(), get()) }
    single { CoroutinePlayAttempter() }
    factory {
        MediaPlayerControlManager(
                androidContext(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
        )
    }
}

val serviceModule = module {
    single { ServiceManager(androidContext(), get()) }
}

val receiverModule = module {
    single { BTConnectionReceiver() }
    single { BTStateChangedReceiver() }
    single { PowerConnectionReceiver() }
}

val helperModule = module {
    single { PackageHelper(get(), get()) }
    single { TelephoneHelper(get(), get()) }
    single { LaunchHelper(androidContext(), get(), get()) }
    single { MediaSessionTokenHelper(androidContext(), get()) }
    single { PreferencesHelper(get()) }
    single { ToastHelper(androidContext()) }
    single { BluetoothDeviceHelper(get()) }
    single { FirebaseHelper(androidContext()) }
    single { SerializationHelper(get()) }
}

val notificationModule = module {
    single { BAPMNotification(androidContext(), get()) }
}

val permissionModule = module {
    single { PermissionManager(androidContext(), get()) }
}

val bluetoothModule = module {
    single {
        BTDisconnectActions(
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
                get()
        )
    }
    single {
        BTConnectionManager(
                androidContext(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
        )
    }
}

val wrapperModule = module {
    factory { SystemServicesWrapper(androidContext()) }
    factory { PackageManagerWrapper(androidContext()) }
    factory { StringResourceWrapper(androidContext()) }
}

val factoryModule = module {
    single { PlayerControlsFactory(androidContext(), get()) }
    single { MapLauncherFactory(get(), get()) }
}

val detectorModule = module {
    single { ForegroundDetectorFactory(get()) }
}

val powerModule = module {
    single { PowerInfo(androidContext(), get()) }
    single { OnPowerConnectedAction(get(), get(), get()) }
}

