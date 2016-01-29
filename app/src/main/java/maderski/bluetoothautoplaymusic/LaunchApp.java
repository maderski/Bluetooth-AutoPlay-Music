package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.MediaStore;
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

    public static void launchMaps(Context context){
        boolean isEnabled = BAPMPreferences.getLaunchGoogleMaps(context);
        String mapAppName = BAPMPreferences.getMapsChoice(context);

        if (isEnabled) {
            launch(context, mapAppName);
        }else{
            Log.i(TAG, "Maps Launch is OFF");
        }

    }

    public static void launchSelectedMusicPlayer(Context context){
        boolean isEnabled = BAPMPreferences.getLaunchMusicPlayer(context);
        int index = BAPMPreferences.getSelectedMusicPlayer(context);
        String packageName = VariousLists.listOfInstalledMediaPlayers(context).get(index);

        if(isEnabled) {
            delayMusicAppLaunch(context, 5, packageName);
        }
        else
            Log.i(TAG, "Launch Music Player is OFF");
    }

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

    private static void delayMusicAppLaunch(Context context, int seconds, String packageName){
        final Context ctx = context;
        final String pkgName = packageName;
        seconds = seconds * 1000;
        new CountDownTimer(seconds,
                9999) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                // Not used
            }

            public void onFinish() {
                launch(ctx, pkgName);
                PlayMusic.play(ctx, pkgName);
                //PlayMusic.start(ctx);
                //PlayMusic.play_music(ctx, pkgName);
                delayLaunchMaps(ctx, 2);
                Log.i("Launch Delay: ", "Finished");
            }
        }.start();
    }

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
    }
}

