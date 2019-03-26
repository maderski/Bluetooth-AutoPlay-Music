package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils

/**
 * Created by Jason on 7/23/16.
 */
class PowerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val isBTConnected = BluetoothUtils.isBluetoothA2DPOnCompat(context)
        val powerRequired = BAPMPreferences.getPowerConnected(context)
        val ranActionsOnBtConnect = BAPMDataPreferences.getRanActionsOnBtConnect(context)
        val isHeadphones = BAPMDataPreferences.getIsAHeadphonesDevice(context)

        Log.d(TAG, "Power Connected to a Selected BTDevice")
        Log.d(TAG, "Is BT Connected: " + java.lang.Boolean.toString(isBTConnected))

        if (powerRequired && isBTConnected && ranActionsOnBtConnect.not() && isHeadphones.not()) {
            val launchIntent = Intent()
            launchIntent.action = "maderski.bluetoothautoplaymusic.pluggedinlaunch"
            context.sendBroadcast(launchIntent)
        }
    }

    companion object {
        private const val TAG = "PowerReceiver"
    }

}
