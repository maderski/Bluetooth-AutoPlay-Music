package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Jason on 9/10/16.
 */
public class Permissions {

    public void checkLocationPermission(Activity activity) {
        PackageManager packageManager = activity.getPackageManager();
        int hasPermission = packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                activity.getPackageName());
        //Check if Permission is granted
        if(hasPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
        }
    }

    public boolean isLocationPermissionGranted(Context context){
        PackageManager packageManager = context.getPackageManager();
        int hasPermission = packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                context.getPackageName());
        //Check if Permission is granted
        return hasPermission == PackageManager.PERMISSION_GRANTED;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkDoNotDisturbPermission(final Context context, int seconds){

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        boolean hasDoNotDisturbPerm = notificationManager.isNotificationPolicyAccessGranted();
        if(!hasDoNotDisturbPerm){
            long milliseconds = seconds * 1000;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(android.provider.Settings.
                            ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            };
            handler.postDelayed(runnable, milliseconds);
        }
        return hasDoNotDisturbPerm;
    }
}
