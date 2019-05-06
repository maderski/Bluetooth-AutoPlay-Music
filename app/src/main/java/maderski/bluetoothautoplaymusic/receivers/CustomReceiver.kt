package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetoothactions.BTConnectActions

/**
 * Created by Jason on 7/28/16.
 */
class CustomReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action ?: ""
            performAction(action, context.applicationContext)
        }
    }

    private fun performAction(action: String, context: Context) {
        val btConnectActions = BTConnectActions(context)
        val firebaseHelper = FirebaseHelper(context)

        when (action) {
            ACTION_OFF_TELE_LAUNCH -> {
                Log.d(TAG, "OFF_TELE_LAUNCH")
                //Calling actionsOnBTConnect cause onBTConnect already ran
                btConnectActions.actionsOnBTConnect()
                firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.TELEPHONE)
            }
        }
    }

    companion object {
        const val TAG = "CustomReceiver"

        const val ACTION_OFF_TELE_LAUNCH = "maderski.bluetoothautoplaymusic.offtelephonelaunch"
    }
}
