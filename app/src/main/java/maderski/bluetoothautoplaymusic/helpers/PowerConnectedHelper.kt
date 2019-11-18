package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.receivers.PowerConnectionReceiver
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils
import org.koin.core.KoinComponent
import org.koin.core.inject

class PowerConnectedHelper(private val context: Context) : KoinComponent {
    private val dataPreferences: BAPMDataPreferences by inject()

    fun performConnectActions() {
        val isBTConnected = BluetoothUtils.isBluetoothA2DPOnCompat(context.applicationContext)
        val isHeadphones = dataPreferences.getIsAHeadphonesDevice()
        Log.d(PowerConnectionReceiver.TAG, "is BTConnected: $isBTConnected")
        if (isBTConnected && !isHeadphones) {
            val btConnectActions = BTConnectActions(context)
            val firebaseHelper = FirebaseHelper(context)

            Log.d(PowerConnectionReceiver.TAG, "POWER_LAUNCH")
            btConnectActions.onBTConnect()
            firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.POWER)
        }
    }
}