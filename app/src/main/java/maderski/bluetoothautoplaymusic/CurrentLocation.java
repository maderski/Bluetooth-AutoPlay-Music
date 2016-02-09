package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Jason on 2/7/16.
 */
public class CurrentLocation {

    private final static String TAG = CurrentLocation.class.getName();

    private Location location;

    public CurrentLocation(Context context){

        //Get Permission
        PackageManager packageManager = context.getPackageManager();
        int hasPermission = packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                context.getPackageName());
        //Check if Permission is granted
        if(hasPermission == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else {
            Log.e(TAG, "Need GPS Permission!!!");
        }
    }

    public String getLatitude(){
        return Double.toString(location.getLatitude()).trim();
    }

    public String getLongitude(){
        return Double.toString(location.getLongitude()).trim();
    }
}
