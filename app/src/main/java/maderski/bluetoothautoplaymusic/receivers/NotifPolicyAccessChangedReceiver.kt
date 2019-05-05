package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences

/**
 * Created by Jason on 2/12/17.
 */

class NotifPolicyAccessChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (intent.action != null) {
                Log.d(TAG, "ACTION: ${intent.action}")
                val ringerControl = RingerControl(context)
                BAPMDataPreferences.setCurrentRingerSet(context.applicationContext, ringerControl.ringerSetting())
                ringerControl.soundsOFF()
                context.applicationContext.unregisterReceiver(this)
            }
        }
    }

    companion object {
        private const val TAG = "NotifPolicyAccessChange"
    }
}
