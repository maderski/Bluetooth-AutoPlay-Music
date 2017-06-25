package maderski.bluetoothautoplaymusic.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

import java.util.List;

import maderski.bluetoothautoplaymusic.BuildConfig;

/**
 * Created by Jason on 6/6/17.
 */

public class ServiceUtils {
    public static void startService(Context context, Class<?> serviceClass, String tag) {
        if(BuildConfig.DEBUG){
            Toast.makeText(context, serviceClass.getName() + "STARTED", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(context, serviceClass);
        intent.addCategory(tag);
        context.startService(intent);
    }

    public static void stopService(Context context, Class<?> serviceClass, String tag) {
        if(BuildConfig.DEBUG){
            Toast.makeText(context, serviceClass.getName() + "STOPPED", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(context, serviceClass);
        intent.addCategory(tag);
        context.stopService(intent);
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())){
                return true;
            }
        }
        return false;
    }
}
