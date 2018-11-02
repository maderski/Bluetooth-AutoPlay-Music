package maderski.bluetoothautoplaymusic.controls.wakelockcontrol;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;

/**
 * Created by Jason on 1/5/16.
 */

//Uses the singleton pattern, only want one instance of ScreenONLock
public class ScreenONLock {

    private static final String TAG = ScreenONLock.class.getName();
    private static final ScreenONLock instance = new ScreenONLock();

    private PowerManager.WakeLock mWakeLock;

    private ScreenONLock(){}

    public static ScreenONLock getInstance(){
        return instance;
    }

    //Enable WakeLock
    public void enableWakeLock(Context context){
        //Set Screen Brightness(Dim: 6 / Bright: 10)
        int screenBrightness;

        if(BAPMPreferences.INSTANCE.getAutoBrightness(context)) {
            screenBrightness = isDark(context);
        } else {
            screenBrightness = getManualScreenBrightness(context);
        }

        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                screenBrightness, "Stay ON");

        try{
            mWakeLock.acquire();
            Log.d(TAG, "Wakelock enabled");
        }catch (Exception e){
            Log.e(TAG, "WakeLock Error: " + e.getMessage());
        }
    }

    private int getManualScreenBrightness(Context context){
        int screenBrightness;

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int currentTime = (hour * 100) + minute;
        int brightSetTime = BAPMPreferences.INSTANCE.getBrightTime(context);
        int dimSetTime = BAPMPreferences.INSTANCE.getDimTime(context);

        if (currentTime >= dimSetTime || currentTime <= brightSetTime){
            screenBrightness = 6;
        }else
            screenBrightness = 10;

        return screenBrightness;
    }

    //Disable and release WakeLock
    public void releaseWakeLock(){
        if (mWakeLock != null && mWakeLock.isHeld()) {
            try {
                mWakeLock.release();
                mWakeLock = null;
                Log.d(TAG, "Wakelock: " + "disabled");
            } catch (Exception e) {
                Log.e(TAG, "WakeLock: " + "error " + e.getMessage());
            }
        }else{
            Log.i(TAG, "Wakelock: " + "Not Held");
        }
    }

    //Return dimmer screen brightness if Dark
    private int isDark(Context context){
        int dark;
        Calendar c = Calendar.getInstance();
        SunriseSunset ss = new SunriseSunset(context);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int currentTime = (hour * 100) + minute;
        int sunriseTime = ss.getSunrise();
        int sunsetTime = ss.getSunset();

        Log.d(TAG, "Current: " + Integer.toString(currentTime) +
                " SR: " + Integer.toString(sunriseTime) +
                " SS: " + Integer.toString(sunsetTime));

        if(currentTime >= 1200){
            dark = (currentTime >= sunsetTime) ? 6 : 10;
        } else {
            dark = (currentTime <= sunriseTime) ? 6 : 10;
        }

        Log.d(TAG, "dark: " + Integer.toString(dark));
        return dark;
    }

    public boolean wakeLockHeld(){
        return mWakeLock != null && mWakeLock.isHeld();
    }

}
