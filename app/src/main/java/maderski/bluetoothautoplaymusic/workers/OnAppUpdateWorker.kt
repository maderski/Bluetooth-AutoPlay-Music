package maderski.bluetoothautoplaymusic.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import maderski.bluetoothautoplaymusic.services.BAPMService
import maderski.bluetoothautoplaymusic.services.ServiceManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class OnAppUpdateWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), KoinComponent {
    private val serviceManager: ServiceManager by inject()
    override fun doWork(): Result {
        serviceManager.startService(BAPMService::class.java, BAPMService.TAG)
        return Result.success()
    }

    companion object {
        const val TAG = "OnUpdateWorker"
    }
}