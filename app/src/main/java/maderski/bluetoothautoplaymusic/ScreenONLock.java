package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Jason on 1/5/16.
 */
public class ScreenONLock {

    private static final String TAG = ScreenONLock.class.getName();

    private static PowerManager.WakeLock wakeLock;
    private static String stayOnTAG = "Stay ON";

    //Enable WakeLock
    public void enableWakeLock(Context context){

        int screenBrightness;

        if(isDark(context)){
            screenBrightness = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }else{
            screenBrightness = PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
        }

        PowerManager pm = (PowerManager)context.getSystemService(context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                screenBrightness, stayOnTAG);
        try{
            wakeLock.acquire();
            if(BuildConfig.DEBUG)
                Log.i("WakeLock ", "enabled");
        }catch (Exception e){
            Log.e("WakeLock Error: ", e.getMessage());
        }
    }

    //Disable and release WakeLock
    public void releaseWakeLock(){
        if (wakeLock != null && wakeLock.isHeld()) {
            try {
                wakeLock.release();
                if(BuildConfig.DEBUG)
                    Log.i("Wakelock: ", "disabled");
            } catch (Exception e) {
                Log.e("WakeLock: ", "error " + e.getMessage());
            }
        }else{
            if(BuildConfig.DEBUG)
                Log.i("Wakelock: ", "Not Held");
        }
    }

    //Return true if Dark
    private boolean isDark(Context context){
        Boolean dark;
        Calendar c = Calendar.getInstance();
        SunriseSunset ss = new SunriseSunset(context);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int currentTime = (hour * 100) + minute;
        int sunriseTime = ss.getSunrise();
        int sunsetTime = ss.getSunset();

        if(BuildConfig.DEBUG)
            Log.i(TAG, "Current: " + Integer.toString(currentTime) +
                " SR: " + Integer.toString(sunriseTime) +
                " SS: " + Integer.toString(sunsetTime));

        if (currentTime >= sunsetTime || currentTime <= sunriseTime){
            dark = true;
        }else
            dark = false;

        if(BuildConfig.DEBUG)
            Log.i(TAG, "dark: " + Boolean.toString(dark));
        return dark;
    }

}
