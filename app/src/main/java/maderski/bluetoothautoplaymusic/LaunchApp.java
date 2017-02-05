package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import java.util.Calendar;

import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.UI.LaunchBAPMActivity;
import maderski.bluetoothautoplaymusic.UI.MainActivity;

/**
 * Created by Jason on 12/8/15.
 */
public class LaunchApp extends PackageTools {

    private static final String TAG = LaunchApp.class.getName();

    private Context context;

    public LaunchApp(Context context){
        super(context);
        this.context = context;
    }

    //Create a delay before the Music App is launched and if enable launchPackage maps
    public void musicPlayerLaunch(int seconds){
        final String pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context);
        seconds = seconds * 1000;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                launchPackage(pkgName);
            }
        };
        handler.postDelayed(runnable, seconds);
    }

    //Launch Maps or Waze with a delay
    public void launchMaps(int seconds){
        boolean canLaunchToday = canLaunchOnThisDay(context);
        if(canLaunchToday) {
            final Context ctx = context;
            seconds = seconds * 1000;

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String mapAppName = BAPMPreferences.getMapsChoice(ctx);
                    launchPackage(mapAppName);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "delayLaunchmaps started");
                }
            };
            handler.postDelayed(runnable, seconds);
        }
    }

    public void launchBAPMActivity(){
        Intent[] intentArray = new Intent[2];
        intentArray[0] = new Intent(context, MainActivity.class);
        intentArray[0].addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intentArray[1] = new Intent(context, LaunchBAPMActivity.class);
        context.startActivities(intentArray);
    }

    public void sendEverythingToBackground(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private boolean canLaunchOnThisDay(Context context){
        Calendar calendar = Calendar.getInstance();
        String today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        boolean canLaunch = BAPMPreferences.getDaysToLaunchMaps(context).contains(today);
        if(BuildConfig.DEBUG) {
            Log.i(TAG, "+++++++++++++++++++++Day of the week: " + today);
            Log.i(TAG, "Can Launch Maps: " + canLaunch);
        }
        return canLaunch;
    }
}

