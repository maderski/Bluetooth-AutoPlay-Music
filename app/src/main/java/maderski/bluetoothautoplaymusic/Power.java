package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by Jason on 2/21/16.
 */
public class Power {

    private static final String TAG = Power.class.getName();
    //Returns true or false depending if battery is plugged in
    public static boolean isPluggedIn(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int chargePlug = 0;
        try {
            chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            boolean wirelessCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;


        if(usbCharge || acCharge || wirelessCharge)
            return true;
        else
            return false;
    }

}
