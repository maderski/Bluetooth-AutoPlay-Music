package maderski.bluetoothautoplaymusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Set;

import maderski.bluetoothautoplaymusic.services.jobservices.StartBAPMServiceJobService;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

/**
 * Created by Jason on 6/10/17.
 */

public class OnAppUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "OnAppUpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Schedule Job to run on update
        ServiceUtils.scheduleJob(context, StartBAPMServiceJobService.class);

        // Sync newly separated Home Work checkboxes
        syncHomeWorkCheckboxes(context);
    }

    private void syncHomeWorkCheckboxes(Context context) {
        boolean hasRan = BAPMPreferences.getUpdateHomeWorkDaysSync(context);
        if(!hasRan) {
            Set<String> daysHomeWorkRan = BAPMPreferences.getHomeDaysToLaunchMaps(context);
            BAPMPreferences.setWorkDaysToLaunchMaps(context, daysHomeWorkRan);
            BAPMPreferences.setUpdateHomeWorkDaysSync(context, true);
            Log.d(TAG, "Work/Home Sync Complete");
        }
    }
}
