package maderski.bluetoothautoplaymusic;

import android.util.Log;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jason on 1/2/16.
 */
public class CalendarAndTimeInfo {

    private final String TAG = CalendarAndTimeInfo.class.getName();

    public void getTimeZone(){
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();

        String timeZone = TimeZone.getDefault().getDisplayName();
        TimeZone tz = TimeZone.getDefault();
        String dl = Boolean.toString(tz.inDaylightTime(date));
        Log.i(TAG, timeZone + " " + dl);
    }

    public void getTime(){

        int hour;
        int minute;
        int AMPM;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        AMPM = c.get(Calendar.AM_PM);


    }

    public boolean isDark(){
        Boolean dark = false;

        return dark;
    }
}
