package maderski.bluetoothautoplaymusic.ui.activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.analytics.constants.ActivityNameConstants;
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;

public class LaunchBAPMActivity extends AppCompatActivity{
    private static final String TAG = "LaunchBAPMActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_bapm);

        // Create Firebase Event
        FirebaseHelper firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.activityLaunched(ActivityNameConstants.LAUNCH_BAPM);

        // Dismiss the keyguard
        dismissKeyGuard();

        // Hide the fake loading screen.  This is used to keep this activity alive while dismissing the keyguard
        sendHomeAppTimer(3);
    }

    //Dismiss the KeyGuard
    private void dismissKeyGuard(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Window window = getWindow();
            if (!BAPMPreferences.INSTANCE.getKeepScreenON(this)) {
                ScreenONLock screenONLock = ScreenONLock.Companion.getInstance();
                screenONLock.enableWakeLock(this);
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                screenONLock.releaseWakeLock();
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        } else {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
                @Override
                public void onDismissCancelled() {
                    super.onDismissCancelled();
                    Log.d(TAG, "KEYGUARD DISMISS CANCELLED");
                }
            });
        }
    }

    private void sendHomeAppTimer(int seconds){
        boolean launchMaps = BAPMPreferences.INSTANCE.getLaunchGoogleMaps(this);
        boolean launchPlayer = BAPMPreferences.INSTANCE.getLaunchMusicPlayer(this);

        if(!launchMaps && !launchPlayer) {
            final Context context = this;
            int milliSeconds = seconds * 1000;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    finish();
                    LaunchAppHelper launchAppHelper = new LaunchAppHelper();
                    launchAppHelper.sendEverythingToBackground(context);
                }
            };
            handler.postDelayed(runnable, milliSeconds);
        }
    }
}
