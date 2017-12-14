package maderski.bluetoothautoplaymusic.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import maderski.bluetoothautoplaymusic.Services.jobservices.StartBAPMServiceJobService;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

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
