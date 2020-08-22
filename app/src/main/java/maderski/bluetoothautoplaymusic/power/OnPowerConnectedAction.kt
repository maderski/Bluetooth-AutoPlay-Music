package maderski.bluetoothautoplaymusic.power

import android.content.Context
import android.util.Log
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.helpers.BluetoothDeviceHelper
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager

class OnPowerConnectedAction(
        private val context: Context,
        private val btConnectActions: BTConnectActions,
        private val firebaseHelper: FirebaseHelper,
        private val bluetoothDeviceHelper: BluetoothDeviceHelper,
        private val serviceManager: ServiceManager
) {
    fun performBTConnectActions() {
        val isBTConnected = bluetoothDeviceHelper.isBluetoothA2DPOnCompat()
        Log.d(PowerConnectionReceiver.TAG, "is BTConnected: $isBTConnected")
        if (isBTConnected) {
            Log.d(PowerConnectionReceiver.TAG, "POWER_LAUNCH")
            serviceManager.updateServiceNotification(context.getString(R.string.bluetooth_device_connected))
            btConnectActions.onBTConnect()
            firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.POWER)
        }
    }
}