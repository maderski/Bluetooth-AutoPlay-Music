package maderski.bluetoothautoplaymusic.bluetoothactions

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log

import maderski.bluetoothautoplaymusic.controls.PlayMusicControl
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.WifiControl
import maderski.bluetoothautoplaymusic.helpers.PermissionHelper
import maderski.bluetoothautoplaymusic.helpers.PowerHelper
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.BAPMNotification
import maderski.bluetoothautoplaymusic.receivers.NotifPolicyAccessChangedReceiver
import maderski.bluetoothautoplaymusic.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.services.WakeLockService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.helpers.TelephoneHelper
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 6/3/17.
 */

class BTConnectActions(private val context: Context) {
    private val mBAPMNotification: BAPMNotification = BAPMNotification()
    private val mVolumeControl: VolumeControl = VolumeControl(context)
    private val mPlayMusicControl: PlayMusicControl = PlayMusicControl(context)
    private val mLaunchAppHelper: LaunchAppHelper = LaunchAppHelper()

    fun onBTConnect() {
        val waitTillOffPhone = BAPMPreferences.getWaitTillOffPhone(context)

        if (waitTillOffPhone) {
            val telephoneHelper = TelephoneHelper(context)
            if (PowerHelper.isPluggedIn(context)) {
                if (telephoneHelper.isOnCall) {
                    Log.d(TAG, "ON a call")
                    //Run checkIfOnPhone
                    telephoneHelper.checkIfOnPhone(mVolumeControl)
                } else {
                    Log.d(TAG, "NOT on a call")
                    actionsOnBTConnect()
                }
            } else {
                if (telephoneHelper.isOnCall) {
                    mBAPMNotification.launchBAPM(context)
                } else {
                    actionsOnBTConnect()
                }
            }
        } else {
            actionsOnBTConnect()
        }
    }

    //Creates mNotification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    fun actionsOnBTConnect() {
        val unlockScreen = BAPMPreferences.getUnlockScreen(context)

        showBTAMNotification()
        setVolumeToMax()
        turnTheScreenOn()

        if (unlockScreen) {
            performActionsDelay()
        } else {
            launchMusicMapApp()
            autoPlayMusic(6)
            setWifiOff()
            putPhoneInDoNotDisturb()
        }

        BAPMDataPreferences.setRanActionsOnBtConnect(context, true)
        ServiceUtils.stopService(context, OnBTConnectService::class.java, OnBTConnectService.TAG)
    }

    private fun performActionsDelay() {
        val seconds = 30000

        object : CountDownTimer(seconds.toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val isDeviceLocked = keyguardManager.isDeviceLocked
                Log.d(TAG, "IS DEVICE LOCKED: " + isDeviceLocked.toString())
                if (!isDeviceLocked && millisUntilFinished < 28000) {
                    cancel()
                    onFinish()
                }
                Log.d(TAG, "LOCKED mills left to check: " + millisUntilFinished.toString())
            }

            override fun onFinish() {
                unlockTheScreen()
                launchMusicMapApp()
                autoPlayMusic(6)
                setWifiOff()
                putPhoneInDoNotDisturb()
            }
        }.start()
    }

    private fun showBTAMNotification() {
        val mapChoice = BAPMPreferences.getMapsChoice(context)
        val canShowNotification = BAPMPreferences.getShowNotification(context)
        if (canShowNotification) {
            mBAPMNotification.BAPMMessage(context, mapChoice)
        }
    }

    private fun turnTheScreenOn() {
        val screenON = BAPMPreferences.getKeepScreenON(context)
        if (screenON) {
            ServiceUtils.startService(context, WakeLockService::class.java, WakeLockService.TAG)
        }
    }

    private fun unlockTheScreen() {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isKeyguardLocked = keyguardManager.isKeyguardLocked
        Log.d(TAG, "Is keyguard locked: " + java.lang.Boolean.toString(isKeyguardLocked))
        if (isKeyguardLocked) {
            mLaunchAppHelper.launchBAPMActivity(context)
        }
    }

    private fun setVolumeToMax() {
        val volumeMAX = BAPMPreferences.getMaxVolume(context)
        if (volumeMAX) {
            val handler = Handler()
            val runnable = Runnable { mVolumeControl.checkSetMAXVol(4) }
            handler.postDelayed(runnable, 3000)
        }
    }

    private fun autoPlayMusic(checkToPlaySeconds: Int) {
        val playMusic = BAPMPreferences.getAutoPlayMusic(context)
        if (playMusic) {
            mPlayMusicControl.checkIfPlaying(context, checkToPlaySeconds)
        }
    }

    private fun launchMusicMapApp() {
        val launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context)
        val launchMaps = BAPMPreferences.getLaunchGoogleMaps(context)
        val mapsCanLaunch = mLaunchAppHelper.canMapsLaunchNow(context)

        if (launchMusicPlayer && !launchMaps || launchMusicPlayer && !mapsCanLaunch) {
            mLaunchAppHelper.musicPlayerLaunch(context, 3)
        }

        if (launchMaps) {
            mLaunchAppHelper.launchMaps(context, 3)
        }
    }

    private fun setWifiOff() {
        val isWifiOffDevice = BAPMDataPreferences.getIsTurnOffWifiDevice(context)
        if (isWifiOffDevice) {
            val morningStartTime = BAPMPreferences.getMorningStartTime(context)
            val morningEndTime = BAPMPreferences.getMorningEndTime(context)

            val current24hrTime = TimeHelper.current24hrTime

            val timeHelperMorning = TimeHelper(morningStartTime, morningEndTime, current24hrTime)
            val canLaunch = timeHelperMorning.isWithinTimeSpan
            val directionLocation = if (canLaunch) LaunchAppHelper.WORK else LaunchAppHelper.HOME

            val canChangeWifiState = !BAPMPreferences.getWifiUseMapTimeSpans(context) || canLaunch && mLaunchAppHelper.canLaunchOnThisDay(context, directionLocation)
            if (canChangeWifiState && WifiControl.isWifiON(context)) {
                WifiControl.wifiON(context, false)
            }
        }
    }

    private fun putPhoneInDoNotDisturb() {
        val ringerControl = RingerControl(context)
        val priorityMode = BAPMPreferences.getPriorityMode(context)

        if (priorityMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val hasDoNotDisturbPerm = PermissionHelper.checkDoNotDisturbPermission(context, 10)
                if (hasDoNotDisturbPerm) {
                    BAPMDataPreferences.setCurrentRingerSet(context, ringerControl.ringerSetting())
                    ringerControl.soundsOFF()
                } else {
                    val broadcastReceiver = NotifPolicyAccessChangedReceiver()
                    val intentFilter = IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED)
                    context.applicationContext.registerReceiver(broadcastReceiver, intentFilter)
                }
            } else {
                BAPMDataPreferences.setCurrentRingerSet(context, ringerControl.ringerSetting())
                ringerControl.soundsOFF()
            }
        }
    }

    companion object {
        private const val TAG = "BTConnectActions"
    }
}
