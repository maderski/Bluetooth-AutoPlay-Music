package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
        Log.i("Music pLayer intent: ", pkg + " started");
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(LaunchIntent);
    }

    public static void launchMaps(Context context){
        boolean isEnabled = BAPMPreferences.getLaunchGoogleMaps(context);
        String mapAppName = BAPMPreferences.getMapsChoice(context);

        if (isEnabled) {
            if(mapAppName.equals("Waze")){
                if(checkPkgOnPhone(context, "com.waze")){
                    launch(context, "com.waze");
                }
            }else{
                launch(context, "com.google.android.apps.maps");
            }
        }else{
            Log.i(TAG, "Maps Launch is OFF");
        }

    }

    public static void launchSelectedMusicPlayer(Context context){
        boolean isEnabled = BAPMPreferences.getLaunchMusicPlayer(context);
        int index = BAPMPreferences.getSelectedMusicPlayer(context);
        String packageName = VariousLists.listOfInstalledMediaPlayers(context).get(index);

        if(isEnabled) {
            launch(context, packageName);
            PlayMusic.start(context);
        }
        else
            Log.i(TAG, "Launch Music Player is OFF");
    }

    public static boolean checkPkgOnPhone(Context context, String pkg){
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        if(appInfo.contains(pkg))
            return true;
        else
            return false;
    }

}

