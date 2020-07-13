package maderski.bluetoothautoplaymusic.bluetooth.btactions

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
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PreferencesHelper
import maderski.bluetoothautoplaymusic.helpers.TelephoneHelper
import maderski.bluetoothautoplaymusic.launchers.MapAppLauncher
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import maderski.bluetoothautoplaymusic.receivers.NotifPolicyAccessChangedReceiver
import maderski.bluetoothautoplaymusic.services.WakeLockService
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 6/3/17.
 */

class BTConnectActions(
        private val context: Context,
        private val volumeControl: VolumeControl,
        private val launchHelper: LaunchHelper,
        private val mediaPlayerControlManager: MediaPlayerControlManager,
        private val serviceManager: ServiceManager,
        private val telephoneHelper: TelephoneHelper,
        private val preferencesHelper: PreferencesHelper,
        private val ringerControl: RingerControl,
        private val mapAppLauncher: MapAppLauncher,
        private val systemServicesWrapper: SystemServicesWrapper,
        private val permissionManager: PermissionManager
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
        val isOnCall = telephoneHelper.isOnCall()

        if (isOnCall) {
            Log.d(TAG, "ON a call")
            telephoneHelper.checkIfOnPhone(volumeControl, ringerControl)
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

        setVolumeToMax()
        turnTheScreenOn()

        if (unlockScreen) {
            performActionsDelay()
        } else {
            launchMusicPlayer()
            launchMapApp()
            autoPlayMusic()
            putPhoneInDoNotDisturb()
        }
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
        putPhoneInDoNotDisturb()
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
        val isLaunchingMaps = preferencesHelper.isLaunchingMaps //&& mapAppLauncher.canMapsLaunchNow()
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

    private fun putPhoneInDoNotDisturb() {
        val priorityMode = preferencesHelper.priorityMode

        if (priorityMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val hasDoNotDisturbPerm = permissionManager.checkDoNotDisturbPermission()
                if (hasDoNotDisturbPerm) {
                    ringerControl.saveCurrentRingerSetting()
                    ringerControl.soundsOFF()
                } else {
                    val broadcastReceiver = NotifPolicyAccessChangedReceiver()
                    val intentFilter = IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED)
                    context.registerReceiver(broadcastReceiver, intentFilter)
                }
            } else {
                ringerControl.saveCurrentRingerSetting()
                ringerControl.soundsOFF()
            }
        }
    }

    companion object {
        private const val TAG = "BTConnectActions"
    }
}
