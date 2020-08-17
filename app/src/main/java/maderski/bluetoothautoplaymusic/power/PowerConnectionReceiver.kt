package maderski.bluetoothautoplaymusic.power

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject

class PowerConnectionReceiver : BroadcastReceiver(), KoinComponent {
    private val onPowerConnectedAction: OnPowerConnectedAction by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let { action ->
            if (action == Intent.ACTION_POWER_CONNECTED) {
                onPowerConnectedAction.performBTConnectActions()
                context.unregisterReceiver(this)
            }
        }
    }

    companion object {
        const val TAG = "PowerConnectionReceiver"
    }

}