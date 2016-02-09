package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
/**
 * Created by Jason on 2/7/16.
 */
public class SunriseSunset {

    private final static String TAG = SunriseSunset.class.getName();

    private String sunriseTime;
    private String sunsetTime;

    //Get Sunrise and Sunset Times as Strings
    public SunriseSunset(Context context){
        Calendar today = Calendar.getInstance();
        CurrentLocation currentLocation = new CurrentLocation(context);
        Location location = new Location(currentLocation.getLatitude(), currentLocation.getLongitude());
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, getTimeZone());
        Log.i(TAG, "Lat " + currentLocation.getLatitude() + " Long " + currentLocation.getLongitude());
        sunriseTime = calculator.getCivilSunriseForDate(today);
        sunsetTime = calculator.getCivilSunsetForDate(today);
        Log.i(TAG, "sunrise: " + sunriseTime + " sunset: " + sunsetTime);
    }

    //Get Timezone
    private TimeZone getTimeZone(){
        Calendar c = Calendar.getInstance();
        return c.getTimeZone();
    }

    //Return Sunrise time as a single integer number
    public int getSunrise(){
        return getTime(sunriseTime);
    }

    //Return Sunrise time as a single integer number
    public int getSunset(){
        return getTime(sunsetTime);
    }

    //Convert Time String into a single integer number
    private int getTime(String inputTime){
        String time = inputTime;
        String[] split = time.split(":");
        int outputTime = (Integer.parseInt(split[0]) * 100) + Integer.parseInt(split[1]);
        Log.i(TAG, Integer.toString(outputTime));
        return outputTime;
    }
}
