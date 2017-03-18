package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.UI.LaunchBAPMActivity;
import maderski.bluetoothautoplaymusic.UI.MainActivity;

/**
 * Created by Jason on 12/8/15.
 */
public class LaunchApp extends PackageTools {

    private static final String TAG = LaunchApp.class.getName();

    @StringDef({
            DirectionLocations.HOME,
            DirectionLocations.WORK
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DirectionLocations {
        String HOME = "Home";
        String WORK = "Work";
    }

    private String mDirectionLocation = "None";

    public LaunchApp(){
        super();
    }

    //Create a delay before the Music App is launched and if enable launchPackage maps
    public void musicPlayerLaunch(final Context context, int seconds){
        final String pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context);
        seconds = seconds * 1000;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                launchPackage(context, pkgName);
            }
        };
        handler.postDelayed(runnable, seconds);
    }

    //Launch Maps or Waze with a delay
    public void launchMaps(final Context context, int seconds){
        final boolean canLaunchWazeDirections = BAPMPreferences.getCanLaunchDirections(context)
                && BAPMPreferences.getMapsChoice(context).equals(PackageName.WAZE);

        boolean canLaunchToday = canMapsLaunchOnThisDay(context) && canMapsLaunchDuringThisTime(context);
        if(canLaunchToday) {
            seconds = seconds * 1000;

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String mapAppName = BAPMPreferences.getMapsChoice(context);
                    if(canLaunchWazeDirections){
                        String uri = "waze://?favorite=" + mDirectionLocation + "&navigate=yes";
                        Log.d(TAG, "DIRECTIONS LOCATION: " + uri);
                        Uri data = Uri.parse(uri);
                        launchPackage(context, mapAppName, data, Intent.ACTION_VIEW);
                    } else {
                        launchPackage(context, mapAppName);
                    }
                    Log.d(TAG, "delayLaunchmaps started");
                }
            };
            handler.postDelayed(runnable, seconds);
        }
    }

    public void launchBAPMActivity(final Context context){
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent[] intentArray = new Intent[2];
                intentArray[0] = new Intent(context, MainActivity.class);
                intentArray[0].addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intentArray[1] = new Intent(context, LaunchBAPMActivity.class);
                context.startActivities(intentArray);
            }
        };
        handler.postDelayed(runnable, 500);
    }

    public void sendEverythingToBackground(Context context){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private boolean canMapsLaunchOnThisDay(Context context){
        Calendar calendar = Calendar.getInstance();
        String today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        boolean canLaunch = BAPMPreferences.getDaysToLaunchMaps(context).contains(today);
        Log.d(TAG, "Day of the week: " + today);
        Log.d(TAG, "Can Launch Maps: " + canLaunch);

        return canLaunch;
    }

    public boolean canMapsLaunchDuringThisTime(Context context){
        boolean isUseLaunchTimeEnabled = BAPMPreferences.getUseTimesToLaunchMaps(context);
        if(isUseLaunchTimeEnabled) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int currentTime = (hour * 100) + minute;

            int morningStartTime = BAPMPreferences.getMorningStartTime(context);
            int morningEndTime = BAPMPreferences.getMorningEndTime(context);

            int eveningStartTime = BAPMPreferences.getEveningStartTime(context);
            int eveningEndTime = BAPMPreferences.getEveningEndTime(context);

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

            // Set whether to launch HOME or WORK directions
            if(currentTime >= morningStartTime && currentTime <= morningEndTime){
                mDirectionLocation = DirectionLocations.HOME;
            } else if(currentTime >= eveningStartTime && currentTime <= eveningEndTime){
                mDirectionLocation = DirectionLocations.WORK;
            }

            // Return result on whether Waze can launch or not
            return currentTime >= morningStartTime && currentTime <= morningEndTime
                    || currentTime >= eveningStartTime && currentTime <= eveningEndTime;
        }

        return true;
    }

    public void launchWazeDirections(final Context context, final String location){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String uriString = "waze://?favorite=" + location + "&navigate=yes";
                Log.d(TAG, "DIRECTIONS LOCATION: " + uriString);
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                context.sendBroadcast(intent);
            }
        };

        handler.postDelayed(runnable, 4000);
    }

    public void closeWazeOnDisconnect(final Context context){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction("Eliran_Close_Intent");
                context.sendBroadcast(intent);
            }
        };

        handler.postDelayed(runnable, 2000);
    }
}

