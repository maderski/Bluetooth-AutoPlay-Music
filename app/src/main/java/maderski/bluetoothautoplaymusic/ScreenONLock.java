package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Jason on 1/5/16.
 */
public class ScreenONLock {

    private String TAG = ScreenONLock.class.getName();

    //Enable WakeLock
    public void enableWakeLock(Context context){

        int screenBrightness;

        if(isDark(7, 7)){
            screenBrightness = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }else{
            screenBrightness = PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
        }

        PowerManager pm = (PowerManager)context.getSystemService(context.POWER_SERVICE);
        VariableStore.wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                screenBrightness, VariableStore.stayOnTAG);
        try{
            VariableStore.wakeLock.acquire();
            Log.i("WakeLock ", "enabled");
        }catch (Exception e){
            Log.e("WakeLock Error: ", e.getMessage());
        }
    }

    //Disable and release WakeLock
    public void releaseWakeLock(Context context){
        if (VariableStore.wakeLock != null && VariableStore.wakeLock.isHeld()) {
            try {
                VariableStore.wakeLock.release();
                Log.i("Wakelock: ", "disabled");
            } catch (Exception e) {
                Log.e("WakeLock: ", "error " + e.getMessage());
            }
        }
    }

    //Return true if Dark
    private static boolean isDark(int darkEveningHour, int lightMorningHour){
        Boolean dark;
        int hour;
        int AMPM;

        Calendar c = Calendar.getInstance();
        AMPM = c.get(Calendar.AM_PM);
        hour = c.get(Calendar.HOUR);
        Log.i("Time: ", Integer.toString(hour) + " " + Integer.toString(AMPM));

        if (hour >= darkEveningHour && AMPM == 1 || hour <=lightMorningHour && AMPM == 0){

            dark = true;
        }else
            dark = false;

        return dark;
    }

}
