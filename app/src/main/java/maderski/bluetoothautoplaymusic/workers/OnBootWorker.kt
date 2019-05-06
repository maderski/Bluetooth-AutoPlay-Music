package maderski.bluetoothautoplaymusic.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import maderski.bluetoothautoplaymusic.services.BAPMService
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

class OnBootWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        ServiceUtils.startService(applicationContext, BAPMService::class.java, BAPMService.TAG)
        return Result.success()
    }

    companion object {
        const val TAG = "OnBootWorker"
    }
}