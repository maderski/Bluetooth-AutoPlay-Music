package maderski.bluetoothautoplaymusic.bluetooth.btactions

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.WifiControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.receivers.NotifPolicyAccessChangedReceiver
import maderski.bluetoothautoplaymusic.bluetooth.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.helpers.*
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.services.WakeLockService
import maderski.bluetoothautoplaymusic.utils.PermissionUtils
import maderski.bluetoothautoplaymusic.helpers.enums.DirectionLocation
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.launchers.MapAppLauncher
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/3/17.
 */

class BTConnectActions(
        private val context: Context,
        private val volumeControl: VolumeControl,
        private val bapmNotification: BAPMNotification,
        private val launchHelper: LaunchHelper,
        private val mediaPlayerControlManager: MediaPlayerControlManager,
        private val serviceManager: ServiceManager,
        private val powerHelper: PowerHelper,
        private val telephoneHelper: TelephoneHelper,
        private val preferencesHelper: PreferencesHelper,
        private val ringerControl: RingerControl,
        private val mapAppLauncher: MapAppLauncher,
        private val systemServicesWrapper: SystemServicesWrapper,
        private val wifiControl: WifiControl
) {
    fun onBTConnect() {
        val waitTillOffPhone = preferencesHelper.waitTillOffPhone
        if (waitTillOffPhone) {
            actionsWhileOnCall()
        } else {
            actionsOnBTConnect()
        }
    }

    private fun actionsWhileOnCall() {
        val isOnCall = telephoneHelper.isOnCall
        val isPluggedIn = powerHelper.isPluggedIn()

        if (isOnCall) {
            Log.d(TAG, "ON a call")
            // if plugged in run on the phone check else launch notification
            if (isPluggedIn) telephoneHelper.checkIfOnPhone(volumeControl) else bapmNotification.launchBAPM()
        } else {
            Log.d(TAG, "NOT on a call")
            actionsOnBTConnect()
        }
    }

    //Creates notification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    fun actionsOnBTConnect() {
        val unlockScreen = preferencesHelper.unlockScreen

        showBTAMNotification()
        setVolumeToMax()
        turnTheScreenOn()

        if (unlockScreen) {
            performActionsDelay()
        } else {
            launchMusicPlayer()
            launchMapApp()
            autoPlayMusic()
            setWifiOff()
            putPhoneInDoNotDisturb()
        }

        serviceManager.stopService(OnBTConnectService::class.java, OnBTConnectService.TAG)
    }

    private fun performActionsDelay() {
        val isAtLeastAndroid10 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        if (isAtLeastAndroid10) {
            performActions()
        } else {
            Handler(Looper.getMainLooper()).post {
                val milliSeconds = 30000
                object : CountDownTimer(milliSeconds.toLong(), 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        val keyguardManager = systemServicesWrapper.keyguardManager
                        val isDeviceLocked = keyguardManager.isDeviceLocked
                        Log.d(TAG, "IS DEVICE LOCKED: $isDeviceLocked")
                        if (!isDeviceLocked && millisUntilFinished < 28000) {
                            cancel()
                            onFinish()
                        }
                        Log.d(TAG, "LOCKED mills left to check: $millisUntilFinished")
                    }

                    override fun onFinish() {
                        performActions()
                    }
                }.start()
            }
        }
    }

    private fun performActions() {
        unlockTheScreen()
        launchMusicPlayer()
        launchMapApp()
        autoPlayMusic()
        setWifiOff()
        putPhoneInDoNotDisturb()
    }

    private fun showBTAMNotification() {
        val mapChoice = preferencesHelper.mapAppName
        val canShowNotification = preferencesHelper.canShowNotification
        if (canShowNotification) {
            bapmNotification.bapmMessage(mapChoice)
        }
    }

    private fun turnTheScreenOn() {
        val screenON = preferencesHelper.keepScreenON
        if (screenON) {
            serviceManager.startService(WakeLockService::class.java, WakeLockService.TAG)
        }
    }

    private fun unlockTheScreen() {
        val keyguardManager = systemServicesWrapper.keyguardManager
        val isKeyguardLocked = keyguardManager.isKeyguardLocked
        Log.d(TAG, "Is keyguard locked: " + java.lang.Boolean.toString(isKeyguardLocked))
        if (isKeyguardLocked) {
            launchHelper.launchBAPMActivity()
        }
    }

    private fun setVolumeToMax() {
        val volumeMAX = preferencesHelper.volumeMAX
        if (volumeMAX) {
            volumeControl.checkSetMAXVol(4)
        }
    }

    private fun autoPlayMusic() {
        val playMusic = preferencesHelper.canAutoPlayMusic
        if (playMusic) {
            mediaPlayerControlManager.play()
        }
    }

    private fun launchMusicPlayer() {
        val isLaunchingMaps = preferencesHelper.isLaunchingMaps && mapAppLauncher.canMapsLaunchNow()
        val isLaunchingPlayer = preferencesHelper.isLaunchingMusicPlayer
        if (isLaunchingPlayer && !isLaunchingMaps) {
            val musicPlayerPkg = preferencesHelper.musicPlayerPkgName
            launchHelper.launchApp(musicPlayerPkg)
        }
    }

    private fun launchMapApp() {
        val isLaunchingMaps = preferencesHelper.isLaunchingMaps
        if (isLaunchingMaps) {
            mapAppLauncher.mapsLaunch()
        }
    }

    private fun setWifiOff() {
        val isWifiOffDevice = preferencesHelper.isWifiOffDevice
        if (isWifiOffDevice) {
            val morningStartTime = preferencesHelper.morningStartTime
            val morningEndTime = preferencesHelper.morningEndTime

            val current24hrTime = TimeHelper.current24hrTime

            val timeHelperMorning = TimeHelper(morningStartTime, morningEndTime, current24hrTime)
            val canLaunch = timeHelperMorning.isWithinTimeSpan
            val directionLocation = if (canLaunch) DirectionLocation.WORK else DirectionLocation.HOME

            val canChangeWifiState = !preferencesHelper.isUsingWifiMapTimeSpans || canLaunch && mapAppLauncher.canLaunchOnThisDay(directionLocation)
            if (canChangeWifiState && wifiControl.isWifiON()) {
                wifiControl.wifiON(false)
            }
        }
    }

    private fun putPhoneInDoNotDisturb() {
        val priorityMode = preferencesHelper.priorityMode

        if (priorityMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val hasDoNotDisturbPerm = PermissionUtils.checkDoNotDisturbPermission(context, 10)
                if (hasDoNotDisturbPerm) {
                    preferencesHelper.currentRingerSet = ringerControl.ringerSetting()
                    ringerControl.soundsOFF()
                } else {
                    val broadcastReceiver = NotifPolicyAccessChangedReceiver()
                    val intentFilter = IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED)
                    context.applicationContext.registerReceiver(broadcastReceiver, intentFilter)
                }
            } else {
                preferencesHelper.currentRingerSet = ringerControl.ringerSetting()
                ringerControl.soundsOFF()
            }
        }
    }

    companion object {
        private const val TAG = "BTConnectActions"
    }
}
