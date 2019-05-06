package maderski.bluetoothautoplaymusic.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetoothactions.BTConnectActions
import maderski.bluetoothautoplaymusic.receivers.CustomReceiver
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils

class OnPowerConnectedWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val isBTConnected = BluetoothUtils.isBluetoothA2DPOnCompat(applicationContext)
        val isHeadphones = BAPMDataPreferences.getIsAHeadphonesDevice(applicationContext)
        Log.d(CustomReceiver.TAG, "is BTConnected: $isBTConnected")
        if (isBTConnected && !isHeadphones) {
            val btConnectActions = BTConnectActions(applicationContext)
            val firebaseHelper = FirebaseHelper(applicationContext)

            Log.d(CustomReceiver.TAG, "POWER_LAUNCH")
            btConnectActions.onBTConnect()
            firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.POWER)
        }

        return Result.success()
    }

    companion object {
        const val  TAG = "OnPowerConnectedWorker"
    }
}