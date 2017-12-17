package maderski.bluetoothautoplaymusic.helpers;

import java.util.Calendar;

/**
 * Created by Jason on 3/14/17.
 */

public class TimeHelper {
    public static String get12hrTime(int time){
        // Get minutes
        int tempToGetMinutes = time;
        while(tempToGetMinutes > 60){
            tempToGetMinutes -= 100;
        }
        String minutes = tempToGetMinutes > 9 ? Integer.toString(tempToGetMinutes) : "0" + Integer.toString(tempToGetMinutes);

        // Get hour
        int tempToGetHour = (time - tempToGetMinutes)/100;
        if(tempToGetHour == 0){
            tempToGetHour = 24;
        }
        String hour = tempToGetHour > 12 ? Integer.toString(tempToGetHour - 12) : Integer.toString(tempToGetHour);
        String AMPM = tempToGetHour > 11 && tempToGetHour < 24 ? "PM" : "AM";

        return hour + ":" + minutes + " " + AMPM;
    }

    public static int getCurrent24hrTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return (hour * 100) + minute;
    }

    private int mStartTime;
    private int mEndTime;
    private int mCurrentTime;

    public TimeHelper(int startTime, int endTime, int current24hrTime) {
        if(endTime < startTime) {
            if (current24hrTime >= 1200) {
                endTime += 2400;
            } else {
                startTime = 0;
            }
        }

        mEndTime = endTime;
        mStartTime = startTime;
        mCurrentTime = current24hrTime;

    }

    // Return result on whether Maps/Waze can launch or not
    public boolean isWithinTimeSpan(){
        return mCurrentTime >= mStartTime && mCurrentTime <= mEndTime;
    }
}
