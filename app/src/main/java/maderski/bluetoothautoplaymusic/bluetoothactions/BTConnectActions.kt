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
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper.DirectionLocation
import maderski.bluetoothautoplaymusic.helpers.TelephoneHelper
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.receivers.NotifPolicyAccessChangedReceiver
import maderski.bluetoothautoplaymusic.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.services.WakeLockService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.PermissionUtils
import maderski.bluetoothautoplaymusic.utils.PowerUtils
import maderski.bluetoothautoplaymusic.utils.ServiceUtils
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/3/17.
 */

class BTConnectActions(private val context: Context): KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val dataPreferences: BAPMDataPreferences by inject()

    private val mBAPMNotification: BAPMNotification = BAPMNotification(context)
    private val mVolumeControl: VolumeControl = VolumeControl(context)
    private val mPlayMusicControl: PlayMusicControl = PlayMusicControl(context)
    private val mLaunchAppHelper: LaunchAppHelper = LaunchAppHelper(context)

    fun onBTConnect() {
        val waitTillOffPhone = preferences.getWaitTillOffPhone()
        if (waitTillOffPhone) {
            actionsWhileOnCall()
        } else {
            actionsOnBTConnect()
        }
    }

    private fun actionsWhileOnCall() {
        val telephoneHelper = TelephoneHelper(context)
        val isOnCall = telephoneHelper.isOnCall
        val isPluggedIn = PowerUtils.isPluggedIn(context)

        if (isOnCall) {
            Log.d(TAG, "ON a call")
            // if plugged in run on the phone check else launch notification
            if (isPluggedIn) telephoneHelper.checkIfOnPhone(mVolumeControl) else mBAPMNotification.launchBAPM()
        } else {
            Log.d(TAG, "NOT on a call")
            actionsOnBTConnect()
        }
    }

    //Creates mNotification and if set turns screen ON, puts the phone in priority mode,
    //sets the volume to MAX, dismisses the keyguard, Launches the Music Selected Music
    //Player and Launches Maps
    fun actionsOnBTConnect() {
        val unlockScreen = preferences.getUnlockScreen()

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

        dataPreferences.setRanActionsOnBtConnect(true)
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
        val mapChoice = preferences.getMapsChoice()
        val canShowNotification = preferences.getShowNotification()
        if (canShowNotification) {
            mBAPMNotification.bapmMessage(mapChoice)
        }
    }

    private fun turnTheScreenOn() {
        val screenON = preferences.getKeepScreenON()
        if (screenON) {
            ServiceUtils.startService(context, WakeLockService::class.java, WakeLockService.TAG)
        }
    }

    private fun unlockTheScreen() {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isKeyguardLocked = keyguardManager.isKeyguardLocked
        Log.d(TAG, "Is keyguard locked: " + java.lang.Boolean.toString(isKeyguardLocked))
        if (isKeyguardLocked) {
            mLaunchAppHelper.launchBAPMActivity()
        }
    }

    private fun setVolumeToMax() {
        val volumeMAX = preferences.getMaxVolume()
        if (volumeMAX) {
            val handler = Handler()
            val runnable = Runnable { mVolumeControl.checkSetMAXVol(4) }
            handler.postDelayed(runnable, 3000)
        }
    }

    private fun autoPlayMusic(checkToPlaySeconds: Int) {
        val playMusic = preferences.getAutoPlayMusic()
        if (playMusic) {
            mPlayMusicControl.checkIfPlaying(context, checkToPlaySeconds)
        }
    }

    private fun launchMusicMapApp() {
        val launchMusicPlayer = preferences.getLaunchMusicPlayer()
        val launchMaps = preferences.getLaunchGoogleMaps()
        val mapsCanLaunch = mLaunchAppHelper.canMapsLaunchNow()

        if (launchMusicPlayer && !launchMaps || launchMusicPlayer && !mapsCanLaunch) {
            mLaunchAppHelper.musicPlayerLaunch(3)
        }

        if (launchMaps) {
            mLaunchAppHelper.launchMaps(3)
        }
    }

    private fun setWifiOff() {
        val isWifiOffDevice = dataPreferences.getIsTurnOffWifiDevice()
        if (isWifiOffDevice) {
            val morningStartTime = preferences.getMorningStartTime()
            val morningEndTime = preferences.getMorningEndTime()

            val current24hrTime = TimeHelper.current24hrTime

            val timeHelperMorning = TimeHelper(morningStartTime, morningEndTime, current24hrTime)
            val canLaunch = timeHelperMorning.isWithinTimeSpan
            val directionLocation = if (canLaunch) DirectionLocation.WORK else DirectionLocation.HOME

            val canChangeWifiState = !preferences.getWifiUseMapTimeSpans() || canLaunch && mLaunchAppHelper.canLaunchOnThisDay(directionLocation)
            if (canChangeWifiState && WifiControl.isWifiON(context)) {
                WifiControl.wifiON(context, false)
            }
        }
    }

    private fun putPhoneInDoNotDisturb() {
        val ringerControl = RingerControl(context)
        val priorityMode = preferences.getPriorityMode()

        if (priorityMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val hasDoNotDisturbPerm = PermissionUtils.checkDoNotDisturbPermission(context, 10)
                if (hasDoNotDisturbPerm) {
                    dataPreferences.setCurrentRingerSet(ringerControl.ringerSetting())
                    ringerControl.soundsOFF()
                } else {
                    val broadcastReceiver = NotifPolicyAccessChangedReceiver()
                    val intentFilter = IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED)
                    context.applicationContext.registerReceiver(broadcastReceiver, intentFilter)
                }
            } else {
                dataPreferences.setCurrentRingerSet(ringerControl.ringerSetting())
                ringerControl.soundsOFF()
            }
        }
    }

    companion object {
        private const val TAG = "BTConnectActions"
    }
}
