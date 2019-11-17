package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils
import org.koin.core.KoinComponent
import org.koin.core.inject

class PowerConnectionReceiver : BroadcastReceiver(), KoinComponent {
    private val dataPreferences: BAPMDataPreferences by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let { action->
            when (action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    val isBTConnected = BluetoothUtils.isBluetoothA2DPOnCompat(context.applicationContext)
                    val isHeadphones = dataPreferences.getIsAHeadphonesDevice()
                    Log.d(TAG, "is BTConnected: $isBTConnected")
                    if (isBTConnected && !isHeadphones) {
                        val btConnectActions = BTConnectActions(context.applicationContext)
                        val firebaseHelper = FirebaseHelper(context.applicationContext)

                        Log.d(TAG, "POWER_LAUNCH")
                        btConnectActions.onBTConnect()
                        firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.POWER)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "PowerConnectionReceiver"
    }

}