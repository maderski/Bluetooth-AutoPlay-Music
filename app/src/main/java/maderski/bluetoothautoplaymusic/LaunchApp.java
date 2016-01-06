package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jason on 12/8/15.
 */
public class LaunchApp {
    //package name for google play music is: "com.google.android.music"
    //package name for google maps is: "com.google.android.apps.maps"

    //Launches App that was inputted into method
    public static void launch(Context context, String pkg){
        Log.i("Music pLayer intent: ", pkg + " started");
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(LaunchIntent);
    }

}

