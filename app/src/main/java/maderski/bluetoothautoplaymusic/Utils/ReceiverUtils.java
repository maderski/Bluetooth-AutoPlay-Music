package maderski.bluetoothautoplaymusic.Utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by Jason on 3/10/17.
 */

public class ReceiverUtils {
    private static final String TAG = "ReceiverHelper";

    // Start a receiver
    public static void startReceiver(Context context, Class<?> receiverClass) {
        Log.d(TAG, receiverClass.getSimpleName() + " STARTED!");
        try {
            ComponentName receiver = new ComponentName(context, receiverClass);
            PackageManager packageManager = context.getPackageManager();
            packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    // Stop a receiver
    public static void stopReceiver(Context context, Class<?> receiverClass){
        Log.d(TAG, receiverClass.getSimpleName() + " STOPPED!");
        try {
            ComponentName btStateReceiver = new ComponentName(context, receiverClass);
            PackageManager packageManager = context.getPackageManager();
            packageManager.setComponentEnabledSetting(btStateReceiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
}
