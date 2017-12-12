package maderski.bluetoothautoplaymusic.Helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.UI.activities.LaunchBAPMActivity;
import maderski.bluetoothautoplaymusic.UI.activities.MainActivity;

/**
 * Created by Jason on 12/8/15.
 */
public class LaunchAppHelper extends PackageTools {

    private static final String TAG = LaunchAppHelper.class.getName();

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

    private String mDirectionLocation;
    private List<String> mCanLaunchThisTimeLocations = new ArrayList<>();

    public LaunchAppHelper(){
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
        boolean canLaunchMapsNow = canMapsLaunchNow(context);

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
        if(mDirectionLocation != null) {
            mDirectionLocation = mDirectionLocation.equals(DirectionLocations.CUSTOM)
                    ? BAPMPreferences.getCustomLocationName(context) : mDirectionLocation;
        }

        Uri uri;
        if(BAPMPreferences.getMapsChoice(context).equals(PackageName.WAZE)){
            String wazeUri = "waze://?favorite=" + mDirectionLocation + "&navigate=yes";
            uri = Uri.parse(wazeUri);
        } else {
            String mapsUri = "google.navigation:q="+mDirectionLocation;
            uri = Uri.parse(mapsUri);
        }

        Log.d(TAG, "MAPS CHOICE URI:" + uri.toString());
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

    public boolean canMapsLaunchNow(Context context) {
        final boolean canLaunchDuringThisTime = canLaunchDuringThisTime(context);

        boolean canLaunchMapsNow = false;
        if(canLaunchDuringThisTime) {
            for(String location : mCanLaunchThisTimeLocations) {
                canLaunchMapsNow = canLaunchOnThisDay(context, location);
                if(canLaunchMapsNow) {
                    mDirectionLocation = location;
                    break;
                }
            }
        }
        mCanLaunchThisTimeLocations.clear();

        return canLaunchMapsNow;
    }

    public boolean canLaunchOnThisDay(Context context, @DirectionLocations String directionLocation) {
        Calendar calendar = Calendar.getInstance();
        String today = Integer.toString(calendar.get(Calendar.DAY_OF_WEEK));
        boolean canLaunch = false;

        switch(directionLocation) {
            case DirectionLocations.HOME:
                canLaunch = BAPMPreferences.getHomeDaysToLaunchMaps(context).contains(today);
                break;
            case DirectionLocations.WORK:
                canLaunch = BAPMPreferences.getWorkDaysToLaunchMaps(context).contains(today);
                break;
            case DirectionLocations.CUSTOM:
                canLaunch = BAPMPreferences.getCustomDaysToLaunchMaps(context).contains(today);
                break;
        }

        Log.d(TAG, "Day of the week: " + today);
        Log.d(TAG, "Can Launch: " + canLaunch);
        Log.d(TAG, "Direction Location: " + directionLocation);

        return canLaunch;
    }

    public boolean canLaunchDuringThisTime(Context context){
        boolean isUseLaunchTimeEnabled = BAPMPreferences.getUseTimesToLaunchMaps(context);
        if(isUseLaunchTimeEnabled) {

            int morningStartTime = BAPMPreferences.getMorningStartTime(context);
            int morningEndTime = BAPMPreferences.getMorningEndTime(context);

            int eveningStartTime = BAPMPreferences.getEveningStartTime(context);
            int eveningEndTime = BAPMPreferences.getEveningEndTime(context);

            int customStartTime = BAPMPreferences.getCustomStartTime(context);
            int customEndTime = BAPMPreferences.getCustomEndTime(context);

            int current24hrTime = TimeHelper.getCurrent24hrTime();

            TimeHelper timeHelperMorning = new TimeHelper(morningStartTime, morningEndTime, current24hrTime);
            boolean canLaunchWork = timeHelperMorning.isWithinTimeSpan();
            if(canLaunchWork) {
                mCanLaunchThisTimeLocations.add(DirectionLocations.WORK);
            }

            TimeHelper timeHelperEvening = new TimeHelper(eveningStartTime, eveningEndTime, current24hrTime);
            boolean canLaunchHome = timeHelperEvening.isWithinTimeSpan();
            if(canLaunchHome) {
                mCanLaunchThisTimeLocations.add(DirectionLocations.HOME);
            }

            TimeHelper timeHelperCustom = new TimeHelper(customStartTime, customEndTime, current24hrTime);
            boolean canLaunchCustom = timeHelperCustom.isWithinTimeSpan();
            if(canLaunchCustom) {
                mCanLaunchThisTimeLocations.add(DirectionLocations.CUSTOM);
            }

            return canLaunchWork || canLaunchHome || canLaunchCustom;
        } else {
            return true;
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

