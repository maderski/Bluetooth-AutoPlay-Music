package maderski.bluetoothautoplaymusic.helpers

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.bluetooth.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity

/**
 * Created by Jason on 6/1/17.
 */

class BluetoothConnectionManager(
        private val appContext: Context,
        private val serviceManager: ServiceManager,
        private val firebaseHelper: FirebaseHelper,
        private val preferencesHelper: PreferencesHelper
) : ServiceConnection {
    fun a2dpActions(intent: Intent?, bluetoothDevice: BluetoothDevice?) {
        if (intent != null && bluetoothDevice != null) {
            val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0)
            when (state) {
                STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
                STATE_CONNECTED -> {
                    Log.d(TAG, "A2DP CONNECTED")
                    firebaseHelper.connectViaA2DP(bluetoothDevice.name, true)
                    onBTConnect()
                }
                STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
                STATE_DISCONNECTED -> {
                    Log.d(TAG, "A2DP DISCONNECTED")
                    onBTDisconnect()
                }
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        checksBeforeLaunch()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        // no-op
    }
    fun onBTConnect() {
        serviceManager.startService(OnBTConnectService::class.java, OnBTConnectService.TAG, this)
    }

    fun onBTDisconnect() {
        serviceManager.stopService(OnBTConnectService::class.java, OnBTConnectService.TAG)
        val disconnectIntent = Intent(appContext, DisconnectActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        appContext.startActivity(disconnectIntent)
    }

    private fun checksBeforeLaunch() {
        val powerRequired = preferencesHelper.waitTillPowerConnected
        if (!powerRequired) {
            onBTConnect()
        }
    }

    companion object {
        private const val TAG = "BluetoothLaunchHelper"
    }
}
