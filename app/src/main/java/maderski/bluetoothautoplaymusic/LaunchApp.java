package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

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
    public void musicPlayerLaunch(int seconds, boolean _mapsEnabled){
        final String pkgName = BAPMPreferences.getSelectedMusicPlayer(context);
        final boolean mapsEnabled = _mapsEnabled;
        seconds = seconds * 1000;
        new CountDownTimer(seconds,
                9999) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                // Not used
            }

            public void onFinish() {
                launchPackage(pkgName);

                if(mapsEnabled)
                    launchMaps(2);
            }
        }.start();
    }

    //Launch Maps or Waze with a delay
    public void launchMaps(int seconds){
        final Context ctx = context;
        seconds = seconds * 1000;
        new CountDownTimer(seconds,
                9999) // onTick time, not used
        {
            public void onTick(long millisUntilFinished) {
                // Not used
            }

            public void onFinish() {
                String mapAppName = BAPMPreferences.getMapsChoice(ctx);
                launchPackage(mapAppName);
                if(BuildConfig.DEBUG)
                    Log.i("Launch Delay: ", "Finished");
            }
        }.start();
        if(BuildConfig.DEBUG)
            Log.i(TAG, "delayLaunchmaps started");
    }

    //Launch MainActivity, used for unlocking the screen
    public void launchMainActivity(){
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
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
}

