package maderski.bluetoothautoplaymusic.services.jobservices

import android.app.job.JobParameters
import android.app.job.JobService

import maderski.bluetoothautoplaymusic.services.BAPMService
import maderski.bluetoothautoplaymusic.utils.ServiceUtils

/**
 * Created by Jason on 11/11/17.
 */

class StartBAPMServiceJobService : JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        ServiceUtils.startService(applicationContext, BAPMService::class.java, BAPMService.TAG)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return true
    }
}
