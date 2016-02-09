package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.content.Context;
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

    public SunriseSunset(Context context){

    }

    //Get today's date
    private String getDate(){
        String dm;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        c = Calendar.getInstance();
        c.set(year, month, day);
        dm = df.format(c.getTime());
        return dm;
    }

    private String getTimeZoneID(){
        Calendar c = Calendar.getInstance();
        return c.getTimeZone().getDisplayName();

    }

    private TimeZone getTimeZone(){
        Calendar c = Calendar.getInstance();
        return c.getTimeZone();
    }

    public void getSunrise(Context context){
        CurrentLocation currentLocation = new CurrentLocation(context);
        Location location = new Location(currentLocation.getLatitude(), currentLocation.getLongitude());
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, getTimeZone());
        Calendar today = Calendar.getInstance();
        String sunriseTime = calculator.getCivilSunriseForDate(today);
        String sunsetTime = calculator.getCivilSunsetForDate(today);
        Log.i(TAG, "sunrise: " + sunriseTime + " sunset: " + sunsetTime);
    }
}
