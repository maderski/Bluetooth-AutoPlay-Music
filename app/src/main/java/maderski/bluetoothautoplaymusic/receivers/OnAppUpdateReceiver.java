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
        if(intent != null) {
            String action = intent.getAction();
            if(action != null && action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                // Schedule Job to run on update
                ServiceUtils.INSTANCE.scheduleJob(context, StartBAPMServiceJobService.class);

                // Sync newly separated Home Work checkboxes
                syncHomeWorkCheckboxes(context);
            }
        }
    }

    private void syncHomeWorkCheckboxes(Context context) {
        boolean hasRan = BAPMPreferences.INSTANCE.getUpdateHomeWorkDaysSync(context);
        if(!hasRan) {
            Set<String> daysHomeWorkRan = BAPMPreferences.INSTANCE.getHomeDaysToLaunchMaps(context);
            BAPMPreferences.INSTANCE.setWorkDaysToLaunchMaps(context, daysHomeWorkRan);
            BAPMPreferences.INSTANCE.setUpdateHomeWorkDaysSync(context, true);
            Log.d(TAG, "Work/Home Sync Complete");
        }
    }
}
