package maderski.bluetoothautoplaymusic.power

import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.helpers.BluetoothDeviceHelper
import maderski.bluetoothautoplaymusic.receivers.PowerConnectionReceiver

class OnPowerConnectedAction(
        private val btConnectActions: BTConnectActions,
        private val firebaseHelper: FirebaseHelper,
        private val bluetoothDeviceHelper: BluetoothDeviceHelper
) {
    fun performBTConnectActions() {
        val isBTConnected = bluetoothDeviceHelper.isBluetoothA2DPOnCompat()
        Log.d(PowerConnectionReceiver.TAG, "is BTConnected: $isBTConnected")
        if (isBTConnected) {
            Log.d(PowerConnectionReceiver.TAG, "POWER_LAUNCH")
            btConnectActions.onBTConnect()
            firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.POWER)
        }
    }
}