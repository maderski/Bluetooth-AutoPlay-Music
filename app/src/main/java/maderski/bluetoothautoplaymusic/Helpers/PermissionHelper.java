package maderski.bluetoothautoplaymusic.Helpers;

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
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;

/**
 * Created by Jason on 9/10/16.
 */
public class PermissionHelper {
    @StringDef({
            Permission.COARSE_LOCATION
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Permission {
        String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    }

    public static void checkPermission(Activity activity, String permission) {
        PackageManager packageManager = activity.getPackageManager();
        int hasPermission = packageManager.checkPermission(permission,
                activity.getPackageName());
        //Check if Permission is granted
        if(hasPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{permission},
                    PackageManager.PERMISSION_GRANTED);
        }
    }

    public static boolean isPermissionGranted(Context context, String permission){
        PackageManager packageManager = context.getPackageManager();
        int hasPermission = packageManager.checkPermission(permission,
                context.getPackageName());
        //Check if Permission is granted
        return hasPermission == PackageManager.PERMISSION_GRANTED;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkDoNotDisturbPermission(final Context context, int seconds){

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
