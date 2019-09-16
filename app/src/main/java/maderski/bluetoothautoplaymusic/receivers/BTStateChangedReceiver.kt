package maderski.bluetoothautoplaymusic.receivers

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.services.ServiceManager
import maderski.bluetoothautoplaymusic.ui.activities.DisconnectActivity
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 1/28/17.
 */

class BTStateChangedReceiver : BroadcastReceiver(), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (intent.action != null) {
                connectionStateChangedActions(context.applicationContext, intent)
            }
        }
    }

    private fun connectionStateChangedActions(context: Context, intent: Intent) {
        val action = intent.action ?: ""
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED, ignoreCase = true)) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    Log.d(TAG, "Bluetooth off")
                    serviceManager.stopService(BTStateChangedService::class.java, BTStateChangedService.TAG)
                    val disconnectIntent = Intent(context, DisconnectActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(disconnectIntent)
                }
                BluetoothAdapter.STATE_TURNING_OFF -> Log.d(TAG, "Turning Bluetooth off...")
                BluetoothAdapter.STATE_ON -> Log.d(TAG, "Bluetooth on")
                BluetoothAdapter.STATE_TURNING_ON -> Log.d(TAG, "Turning Bluetooth on...")
            }
        }
    }

    companion object {
        private const val TAG = "BTStateChangedReceiver"
    }
}
