package maderski.bluetoothautoplaymusic.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import maderski.bluetoothautoplaymusic.bluetooth.legacy.LegacyDevicesConversionHelper
import maderski.bluetoothautoplaymusic.services.BAPMService
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class OnAppUpdateWorker(
        context: Context,
        workerParams: WorkerParameters
) : Worker(context, workerParams), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    private val legacyConversionHelper: LegacyDevicesConversionHelper by inject()

    override fun doWork(): Result {
        legacyConversionHelper.convertToBAPMDevices()
        serviceManager.startService(BAPMService::class.java, BAPMService.TAG)
        return Result.success()
    }



    companion object {
        const val TAG = "OnUpdateWorker"
    }
}