package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.services.jobservices.StartBAPMServiceJobService
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 1/5/16.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    //Start BAPMService on phone boot
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action != null && action == Intent.ACTION_BOOT_COMPLETED) {
                // Schedule Job to run on boot
                ServiceUtils.scheduleJob(context, StartBAPMServiceJobService::class.java)

                // Check to see Bluetooth is disabled and if it is disabled, then enable it
                BluetoothUtils.enableDisabledBluetooth()

                Log.d(TAG, "BAPM Service Started")
            }
        }
    }

    companion object {

        private const val TAG = "BootCompletedReceiver"
    }
}
