package maderski.bluetoothautoplaymusic.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by Jason on 2/21/16.
 */
public class PowerHelper {
    private static final String TAG = "PowerHelper";

    private PowerHelper(){}

    //Returns true or false depending if battery is plugged in
    public static boolean isPluggedIn(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int chargePlug = 0;
        if(batteryStatus != null) {
            chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }

        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        boolean wirelessCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;

        return usbCharge || acCharge || wirelessCharge;
    }

}
