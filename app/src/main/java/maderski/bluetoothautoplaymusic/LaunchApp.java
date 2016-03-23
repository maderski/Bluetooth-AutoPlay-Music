package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.List;

/**
 * Created by Jason on 12/8/15.
 */
public class LaunchApp {

    private static String TAG = LaunchApp.class.getName();
    //package name for google play music is: "com.google.android.music"
    //package name for google maps is: "com.google.android.apps.maps"
    //package name for waze: "com.waze"

    //Launches App that was inputted into method
    public static void launch(Context context, String pkg){
        Log.i("Package intent: ", pkg + " started");
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(LaunchIntent);
    }

    //Launches Maps or Waze
    public static void launchMaps(Context context){
        String mapAppName = BAPMPreferences.getMapsChoice(context);
        launch(context, mapAppName);
    }

    //Returns true if Package is on phone
    public static boolean checkPkgOnPhone(Context context, String pkg){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(pkg))
                return true;
        }
        return false;
    }

    //Create a delay before the Music App is launched and if enable launch maps
    public static void musicPlayerLaunch(Context context, int seconds, boolean _mapsEnabled){
        int index = BAPMPreferences.getSelectedMusicPlayer(context);

        final Context ctx = context;
        final String pkgName = VariousLists.listOfInstalledMediaPlayers(context).get(index);
        final boolean playMusic = BAPMPreferences.getAutoPlayMusic(context);
        final boolean mapsEnabled = _mapsEnabled;
        seconds = seconds * 1000;
        new CountDownTimer(seconds,
                9999) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                // Not used
            }

            public void onFinish() {
                launch(ctx, pkgName);

                if(mapsEnabled)
                    delayLaunchMaps(ctx, 2);
            }
        }.start();
    }

    //Create a delay before Maps or Waze is launched
    public static void delayLaunchMaps(Context context, int seconds){
        final Context ctx = context;
        seconds = seconds * 1000;
        new CountDownTimer(seconds,
                9999) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                // Not used
            }

            public void onFinish() {
                launchMaps(ctx);
                Log.i("Launch Delay: ", "Finished");
            }
        }.start();
        Log.i(TAG, "delayLaunchmaps started");
    }
}

