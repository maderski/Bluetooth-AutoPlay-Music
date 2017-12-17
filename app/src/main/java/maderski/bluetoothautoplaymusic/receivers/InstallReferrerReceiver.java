package maderski.bluetoothautoplaymusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import maderski.bluetoothautoplaymusic.services.jobservices.StartBAPMServiceJobService;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

/**
 * Created by Jason on 12/14/17.
 */

public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Schedule Job to run on install
        ServiceUtils.scheduleJob(context, StartBAPMServiceJobService.class);
    }
}
