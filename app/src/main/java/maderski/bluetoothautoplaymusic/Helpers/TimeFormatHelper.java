package maderski.bluetoothautoplaymusic.Helpers;

/**
 * Created by Jason on 3/14/17.
 */

public class TimeFormatHelper {
    public static String get12hrTime(int time){
        // Get minutes
        int tempToGetMinutes = time;
        while(tempToGetMinutes > 60){
            tempToGetMinutes -= 100;
        }
        String minutes = tempToGetMinutes > 10 ? Integer.toString(tempToGetMinutes) : "0" + Integer.toString(tempToGetMinutes);

        // Get hour
        int tempToGetHour = (time - tempToGetMinutes)/100;
        if(tempToGetHour == 0){
            tempToGetHour = 24;
        }
        String hour = tempToGetHour > 12 ? Integer.toString(tempToGetHour - 12) : Integer.toString(tempToGetHour);
        String AMPM = tempToGetHour > 11 && tempToGetHour < 24 ? "PM" : "AM";

        return hour + ":" + minutes + " " + AMPM;
    }
}
