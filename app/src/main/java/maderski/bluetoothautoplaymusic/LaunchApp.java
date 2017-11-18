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

import maderski.bluetoothautoplaymusic.Helpers.TimeHelper;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.UI.activities.LaunchBAPMActivity;
import maderski.bluetoothautoplaymusic.UI.activities.MainActivity;

/**
 * Created by Jason on 12/8/15.
 */
public class LaunchApp extends PackageTools {

    private static final String TAG = LaunchApp.class.getName();

    @StringDef({
            DirectionLocations.HOME,
            DirectionLocations.WORK,
            DirectionLocations.CUSTOM
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DirectionLocations {
        String HOME = "Home";
        String WORK = "Work";
        String CUSTOM = "Custom";
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
        final boolean canLaunchDirections = BAPMPreferences.getCanLaunchDirections(context);
        final boolean canHomeLaunchToday = canMapsLaunchOnThisDay(context) && canMapsLaunchDuringThisTime(context);
        final boolean canWorkLaunchToday = canWorkLaunchOnThisDay(context) && canMapsLaunchDuringThisTime(context);
        final boolean canCustomLaunchToday = canCustomLaunchOnThisDay(context) && canCustomLocationLaunchDuringThisTime(context);
        final boolean canLaunchMapsNow = canHomeLaunchToday || canWorkLaunchToday || canCustomLaunchToday;

        if(canLaunchMapsNow) {
            seconds = seconds * 1000;

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String mapAppName = BAPMPreferences.getMapsChoice(context);
                    if(canLaunchDirections){
                        Uri data = getMapsChoiceUri(context);
                        launchPackage(context, mapAppName, data, Intent.ACTION_VIEW);
                    } else {
                        determineIfLaunchWithDrivingMode(context, mapAppName);
                    }
                    Log.d(TAG, "delayLaunchmaps started");
                }
            };
            handler.postDelayed(runnable, seconds);
        }
    }

    // If driving mode is enabled and map choice is set to Google Maps, launch Maps in Driving Mode
    private void determineIfLaunchWithDrivingMode(final Context context, final String mapAppName) {
        boolean canLaunchDrivingMode = BAPMPreferences.getLaunchMapsDrivingMode(context) &&
                mapAppName.equals(PackageName.MAPS);
        if(canLaunchDrivingMode){
            Log.d(TAG, "LAUNCH DRIVING MODE");
            Uri data = Uri.parse("google.navigation:/?free=1&mode=d&entry=fnls");
            launchPackage(context, mapAppName, data, Intent.ACTION_VIEW);
        } else {
            launchPackage(context, mapAppName);
        }
    }

    private Uri getMapsChoiceUri(Context context){
        mDirectionLocation = mDirectionLocation.equals(DirectionLocations.CUSTOM)
                ? BAPMPreferences.getCustomLocationName(context) : mDirectionLocation;

        Uri uri;
        if(BAPMPreferences.getMapsChoice(context).equals(PackageName.WAZE)){
            String wazeUri = "waze://?favorite=" + mDirectionLocation + "&navigate=yes";
            uri = Uri.parse(wazeUri);
        } else {
            String mapsUri = "google.navigation:q="+mDirectionLocation;
            uri = Uri.parse(mapsUri);
        }

        return uri;
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
        handler.postDelayed(runnable, 750);
    }

    public void sendEverythingToBackground(Context context){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public boolean canMapsLaunchOnThisDay(Context context){
        Calendar calendar = Calendar.getInstance();
        String today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        boolean canLaunch = BAPMPreferences.getDaysToLaunchMaps(context).contains(today);
        Log.d(TAG, "Day of the week: " + today);
        Log.d(TAG, "Can Launch Maps: " + canLaunch);

        return canLaunch;
    }

    public boolean canWorkLaunchOnThisDay(Context context) {
        Calendar calendar = Calendar.getInstance();
        String today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        boolean canLaunch = BAPMPreferences.getWorkDaysToLaunchMaps(context).contains(today);
        Log.d(TAG, "Day of the week: " + today);
        Log.d(TAG, "Can Launch Custom: " + canLaunch);

        return canLaunch;
    }

    public boolean canCustomLaunchOnThisDay(Context context) {
        Calendar calendar = Calendar.getInstance();
        String today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        boolean canLaunch = BAPMPreferences.getCustomDaysToLaunchMaps(context).contains(today);
        Log.d(TAG, "Day of the week: " + today);
        Log.d(TAG, "Can Launch Custom: " + canLaunch);

        return canLaunch;
    }

    public boolean canMapsLaunchDuringThisTime(Context context){
        boolean isUseLaunchTimeEnabled = BAPMPreferences.getUseTimesToLaunchMaps(context);
        if(isUseLaunchTimeEnabled) {

            int morningStartTime = BAPMPreferences.getMorningStartTime(context);
            int morningEndTime = BAPMPreferences.getMorningEndTime(context);

            int eveningStartTime = BAPMPreferences.getEveningStartTime(context);
            int eveningEndTime = BAPMPreferences.getEveningEndTime(context);

            int current24hrTime = TimeHelper.getCurrent24hrTime();

            TimeHelper timeHelper = new TimeHelper(morningStartTime, morningEndTime,
                    eveningStartTime, eveningEndTime, current24hrTime);
            mDirectionLocation = timeHelper.getDirectionLocation();

            return timeHelper.isWithinTimeSpan();
        } else {
            return true;
        }
    }

    public boolean canCustomLocationLaunchDuringThisTime(Context context) {
        boolean isUseLaunchTimeEnabled = BAPMPreferences.getUseTimesToLaunchMaps(context);
        if(isUseLaunchTimeEnabled) {
            int customStartTime = BAPMPreferences.getCustomStartTime(context);
            int customEndTime = BAPMPreferences.getCustomEndTime(context);
            int current24hrTime = TimeHelper.getCurrent24hrTime();

            TimeHelper timeHelper = new TimeHelper(customStartTime, customEndTime, current24hrTime);
            mDirectionLocation = timeHelper.getDirectionLocation();

            return timeHelper.isWithinTimeSpan();
        } else {
            return false;
        }
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

