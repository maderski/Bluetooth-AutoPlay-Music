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
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTConnectActions
import maderski.bluetoothautoplaymusic.bluetooth.services.OnBTConnectService
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import java.lang.ref.WeakReference

/**
 * Created by Jason on 6/1/17.
 */

class BluetoothConnectHelper(
        private val appContext: WeakReference<Context>,
        private val serviceManager: ServiceManager,
        private val firebaseHelper: FirebaseHelper,
        private val volumeControl: VolumeControl,
        private val btConnectActions: BTConnectActions,
        private val preferencesHelper: PreferencesHelper
) : ServiceConnection {
    fun a2dpActions(intent: Intent?, bluetoothDevice: BluetoothDevice?) {
        if (intent != null && bluetoothDevice != null) {
            val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0)
            when (state) {
                STATE_CONNECTING -> Log.d(TAG, "A2DP CONNECTING")
                STATE_CONNECTED -> {
                    Log.d(TAG, "A2DP CONNECTED")
                    volumeControl.saveOriginalVolume()
                    firebaseHelper.connectViaA2DP(bluetoothDevice.name, true)

                    serviceManager.startService(OnBTConnectService::class.java, OnBTConnectService.TAG, this)
                }
                STATE_DISCONNECTING -> Log.d(TAG, "A2DP DISCONNECTING")
                STATE_DISCONNECTED -> {
                    Log.d(TAG, "A2DP DISCONNECTED")
                    btDisconnectActions(bluetoothDevice, state)
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

    private fun btDisconnectActions(bluetoothDevice: BluetoothDevice, state: Int) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Device disconnected: ${bluetoothDevice.name}")
        }

        serviceManager.stopService(OnBTConnectService::class.java, OnBTConnectService.TAG)

        val context = appContext.get()
        context?.let {
            val disconnectIntent = Intent(it, DisconnectActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            it.startActivity(disconnectIntent)
        }
    }

    private fun checksBeforeLaunch() {
        val powerRequired = preferencesHelper.waitTillPowerConnected
        if (!powerRequired) {
            btConnectActions.onBTConnect()
        }
    }

    companion object {
        private const val TAG = "BluetoothLaunchHelper"
    }
}
