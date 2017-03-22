package maderski.bluetoothautoplaymusic.Helpers;

import java.util.Calendar;

import maderski.bluetoothautoplaymusic.LaunchApp;

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

    private int mMorningStartTime;
    private int mMorningEndTime;
    private int mEveningStartTime;
    private int mEveningEndTime;
    private int mCurrentTime;

    public TimeHelper(int morningStartTime, int morningEndTime, int eveningStartTime, int eveningEndTime){
        int currentTime = getCurrent24hrTime();

        // Check if the EndTime is less than the StartTime, this means end time was set for early morning
        if (morningEndTime < morningStartTime) {
            if(currentTime >= 1200){
                morningEndTime += 2400;
            } else {
                morningStartTime = 0;
            }
        } else if (eveningEndTime < eveningStartTime) {
            if(currentTime >= 1200){
                eveningEndTime += 2400;
            } else {
                eveningStartTime = 0;
            }
        }

        mMorningStartTime = morningStartTime;
        mMorningEndTime = morningEndTime;
        mEveningStartTime = eveningStartTime;
        mEveningEndTime = eveningEndTime;
        mCurrentTime = currentTime;
    }

    public boolean isWithinTimeSpan(){

        // Return result on whether Waze can launch or not
        return mCurrentTime >= mMorningStartTime && mCurrentTime <= mMorningEndTime
                || mCurrentTime >= mEveningStartTime && mCurrentTime <= mEveningEndTime;
    }

    public int getCurrent24hrTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return (hour * 100) + minute;
    }

    public String getDirectionLocation(){
        if(mCurrentTime >= mMorningStartTime && mCurrentTime <= mMorningEndTime){
            return LaunchApp.DirectionLocations.WORK;
        } else if(mCurrentTime >= mEveningStartTime && mCurrentTime <= mEveningEndTime){
            return LaunchApp.DirectionLocations.HOME;
        } else {
            return "None";
        }
    }
}
