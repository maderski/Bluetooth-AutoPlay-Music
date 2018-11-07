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
            performAction(action, context)
        }
    }

    private fun performAction(action: String, context: Context) {
        val btConnectActions = BTConnectActions(context)
        val firebaseHelper = FirebaseHelper(context)

        when (action) {
            ACTION_POWER_LAUNCH -> {
                Log.d(TAG, "POWER_LAUNCH")
                btConnectActions.onBTConnect()
                firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.POWER)
            }
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

        private const val ACTION_POWER_LAUNCH = "maderski.bluetoothautoplaymusic.pluggedinlaunch"
        private const val ACTION_OFF_TELE_LAUNCH = "maderski.bluetoothautoplaymusic.offtelephonelaunch"
    }
}
