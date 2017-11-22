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

    public static int getCurrent24hrTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return (hour * 100) + minute;
    }

    private int mMorningStartTime;
    private int mMorningEndTime;
    private int mEveningStartTime;
    private int mEveningEndTime;
    private int mCustomStartTime;
    private int mCustomEndTime;
    private int mCurrentTime;

    private boolean mIsCustomTime = false;

    public TimeHelper(int startTime, int endTime, int current24hrTime) {
        mIsCustomTime = true;

        if(endTime < startTime) {
            if (current24hrTime >= 1200) {
                endTime += 2400;
            } else {
                startTime = 0;
            }
        }

        mCustomEndTime = endTime;
        mCustomStartTime = startTime;
        mCurrentTime = current24hrTime;

    }

    public TimeHelper(int morningStartTime, int morningEndTime, int eveningStartTime, int eveningEndTime, int current24hrTime){
        mIsCustomTime = false;

        // Check if the EndTime is less than the StartTime, this means end time was set for early morning
        if (morningEndTime < morningStartTime) {
            if(current24hrTime >= 1200){
                morningEndTime += 2400;
            } else {
                morningStartTime = 0;
            }
        } else if (eveningEndTime < eveningStartTime) {
            if(current24hrTime >= 1200){
                eveningEndTime += 2400;
            } else {
                eveningStartTime = 0;
            }
        }

        mMorningStartTime = morningStartTime;
        mMorningEndTime = morningEndTime;
        mEveningStartTime = eveningStartTime;
        mEveningEndTime = eveningEndTime;
        mCurrentTime = current24hrTime;
    }

    public boolean isWithinTimeSpan(){
        // Return result on whether Maps/Waze can launch or not
        if(!mIsCustomTime) {
            return mCurrentTime >= mMorningStartTime && mCurrentTime <= mMorningEndTime
                    || mCurrentTime >= mEveningStartTime && mCurrentTime <= mEveningEndTime;
        } else {
            return mCurrentTime >= mCustomStartTime && mCurrentTime <= mCustomEndTime;
        }
    }

    public String getDirectionLocation(){
        String directionLocation;
        if(mCurrentTime >= mMorningStartTime && mCurrentTime <= mMorningEndTime && !mIsCustomTime){
            directionLocation = LaunchApp.DirectionLocations.WORK;
        } else if(mCurrentTime >= mEveningStartTime && mCurrentTime <= mEveningEndTime && !mIsCustomTime){
            directionLocation = LaunchApp.DirectionLocations.HOME;
        } else if(mCurrentTime >= mCustomStartTime && mCurrentTime <= mCustomEndTime){
            directionLocation =LaunchApp.DirectionLocations.CUSTOM;
        } else {
            directionLocation = "None";
        }

        return directionLocation;
    }
}
