package maderski.bluetoothautoplaymusic.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.bluetooth.legacy.LegacyDevicesConversionHelper
import maderski.bluetoothautoplaymusic.notification.BAPMNotification
import maderski.bluetoothautoplaymusic.services.BAPMService
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper
import org.koin.core.KoinComponent
import org.koin.core.inject

class OnAppUpdateWorker(
        context: Context,
        workerParams: WorkerParameters
) : Worker(context, workerParams), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    private val legacyConversionHelper: LegacyDevicesConversionHelper by inject()
    private val bapmNotification: BAPMNotification by inject()
    private val stringResWrapper: StringResourceWrapper by inject()

    override fun doWork(): Result {
        legacyConversionHelper.convertToBAPMDevices()
        serviceManager.startService(BAPMService::class.java, BAPMService.TAG)
        bapmNotification.launchBAPMNotification(stringResWrapper.newPermissionReqMsg)
        return Result.success()
    }



    companion object {
        const val TAG = "OnUpdateWorker"
    }
}