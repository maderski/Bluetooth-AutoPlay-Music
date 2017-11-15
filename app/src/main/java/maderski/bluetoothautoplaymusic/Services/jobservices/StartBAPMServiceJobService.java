package maderski.bluetoothautoplaymusic.Services.jobservices;

import android.app.job.JobParameters;
import android.app.job.JobService;

import maderski.bluetoothautoplaymusic.Services.BAPMService;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 11/11/17.
 */

public class StartBAPMServiceJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ServiceUtils.startService(getApplicationContext(), BAPMService.class, BAPMService.TAG);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
