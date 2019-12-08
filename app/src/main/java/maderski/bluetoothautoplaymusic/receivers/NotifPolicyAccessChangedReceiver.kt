package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 2/12/17.
 */

class NotifPolicyAccessChangedReceiver : BroadcastReceiver(), KoinComponent {
    private val dataPreferences: BAPMDataPreferences by inject()
    private val ringerControl: RingerControl by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (intent.action != null) {
                Log.d(TAG, "ACTION: ${intent.action}")
                dataPreferences.setCurrentRingerSet(ringerControl.ringerSetting())
                ringerControl.soundsOFF()
                context.applicationContext.unregisterReceiver(this)
            }
        }
    }

    companion object {
        private const val TAG = "NotifPolicyAccessChange"
    }
}
