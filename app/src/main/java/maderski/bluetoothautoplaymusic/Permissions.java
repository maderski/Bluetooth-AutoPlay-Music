package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
}
