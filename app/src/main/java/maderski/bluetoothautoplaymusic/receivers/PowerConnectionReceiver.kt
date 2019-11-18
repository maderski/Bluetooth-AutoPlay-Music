package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import maderski.bluetoothautoplaymusic.helpers.PowerConnectedHelper
import org.koin.core.KoinComponent
import org.koin.core.inject

class PowerConnectionReceiver : BroadcastReceiver(), KoinComponent {
    private val powerConnectedHelper: PowerConnectedHelper by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let { action->
            if (action == Intent.ACTION_POWER_CONNECTED) {
                 powerConnectedHelper.performConnectActions()
            }
        }
    }

    companion object {
        const val TAG = "PowerConnectionReceiver"
    }

}