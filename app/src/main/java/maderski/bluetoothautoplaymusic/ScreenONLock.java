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

    public void enableWakeLock(Context context){

        int screenBrightness;

        if(isDark()){
            screenBrightness = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }else{
            screenBrightness = PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
        }

        PowerManager pm = (PowerManager)context.getSystemService(context.POWER_SERVICE);
        VariableStore.wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                screenBrightness, VariableStore.stayOnTAG);
        try{
            VariableStore.wakeLock.acquire();
        }catch (Exception e){
            Log.e("WakeLock Error: ", e.getMessage());
        }

    }

    public void releaseWakeLock(){
        if (VariableStore.wakeLock != null && VariableStore.wakeLock.isHeld()) {
            try {
                VariableStore.wakeLock.release();
                Log.i("Wakelock: ", "disabled");
            } catch (Exception e) {
                Log.e("WakeLock: ", "error " + e.getMessage());
            }
        }
    }

    private static boolean isDark(){
        Boolean dark;
        int hour;
        int AMPM;

        Calendar c = Calendar.getInstance();
        AMPM = c.get(Calendar.AM_PM);
        hour = c.get(Calendar.HOUR);
        Log.i("Time: ", Integer.toString(hour) + " " + Integer.toString(AMPM));

        if (hour >= 7 && AMPM == 1 || hour <=7 && AMPM == 0){

            dark = true;
        }else
            dark = false;

        return dark;
    }

}
