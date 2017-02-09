package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jason on 2/7/16.
 */
public class CurrentLocation {

    private static final String TAG = CurrentLocation.class.getName();

    private LocationManager locationManager;
    private Location location;

    //Get course location
    public CurrentLocation(Context context){

        //Get Permission
        PackageManager packageManager = context.getPackageManager();
        int hasPermission = packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                context.getPackageName());
        //Check if Permission is granted
        if(hasPermission == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else {
            Log.e(TAG, "Need GPS Permission!!!");
            Toast.makeText(context, "Need GPS Permission!", Toast.LENGTH_LONG).show();
        }
    }
    //Return Latitude as a String
    public String getLatitude(){
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                return Double.toString(location.getLatitude()).trim();
            else
                return "28.4158";
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        return "28.4158";
    }

    //Return Longitude as a String
    public String getLongitude(){
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                return Double.toString(location.getLongitude()).trim();
            else
                return "-81.2989";
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        return "-81.2989";
    }
}
