package maderski.bluetoothautoplaymusic.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.BTActionsLaunchConstants
import maderski.bluetoothautoplaymusic.bluetoothactions.BTConnectActions
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils
import org.koin.core.KoinComponent
import org.koin.core.inject

class OnPowerConnectedWorker(
        context: Context,
        workerParams: WorkerParameters
) : Worker(context, workerParams), KoinComponent {
    private val dataPreferences: BAPMDataPreferences by inject()

    override fun doWork(): Result {
        val isBTConnected = BluetoothUtils.isBluetoothA2DPOnCompat(applicationContext)
        val isHeadphones = dataPreferences.getIsAHeadphonesDevice()
        Log.d(TAG, "is BTConnected: $isBTConnected")
        if (isBTConnected && !isHeadphones) {
            val btConnectActions = BTConnectActions(applicationContext)
            val firebaseHelper = FirebaseHelper(applicationContext)

            Log.d(TAG, "POWER_LAUNCH")
            btConnectActions.onBTConnect()
            firebaseHelper.bluetoothActionLaunch(BTActionsLaunchConstants.POWER)
        }

        return Result.success()
    }

    companion object {
        const val  TAG = "OnPowerConnectedWorker"
    }
}