package maderski.bluetoothautoplaymusic.receivers

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import maderski.bluetoothautoplaymusic.services.BTDisconnectService
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 1/28/17.
 */

class BTStateChangedReceiver : BroadcastReceiver() {
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
                    ServiceUtils.stopService(context, BTStateChangedService::class.java, BTStateChangedService.TAG)
                    ServiceUtils.startService(context, BTDisconnectService::class.java, BTDisconnectService.TAG)
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
