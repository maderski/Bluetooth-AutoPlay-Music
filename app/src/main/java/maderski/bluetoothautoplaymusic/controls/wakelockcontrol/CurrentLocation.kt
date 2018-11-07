package maderski.bluetoothautoplaymusic.controls.wakelockcontrol

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast

/**
 * Created by Jason on 2/7/16.
 *
 * Get course location
 */
class CurrentLocation(context: Context) {

    private var locationManager: LocationManager? = null
    private var location: Location? = null

    //Return Latitude as a String
    val latitude: String
        get() {
            return try {
                val isProviderEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
                if (isProviderEnabled) {
                    val latitude = location?.latitude ?: 28.4158
                    java.lang.Double.toString(latitude).trim { it <= ' ' }
                } else {
                    "28.4158"
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                "28.4158"
            }
        }

    //Return Longitude as a String
    val longitude: String
        get() {
            return try {
                val isProviderEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
                if (isProviderEnabled) {
                    val longitude = location?.longitude ?: -81.2989
                    java.lang.Double.toString(longitude).trim { it <= ' ' }
                } else {
                    "-81.2989"
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                "-81.2989"
            }
        }

    init {
        //Get Permission
        val packageManager = context.packageManager
        val hasPermission = packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                context.packageName)
        //Check if Permission is granted
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else {
            Log.e(TAG, "Need GPS Permission!!!")
            Toast.makeText(context, "Need GPS Permission!", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "CurrentLocation"
    }
}
