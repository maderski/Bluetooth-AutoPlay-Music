package maderski.bluetoothautoplaymusic.UI.activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.Controls.WakeLockControl.ScreenONLock;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

public class LaunchBAPMActivity extends AppCompatActivity{
    private static final String TAG = "LaunchBAPMActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_bapm);

        // Create Firebase Event
        FirebaseHelper firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.activityLaunched(FirebaseHelper.ActivityName.LAUNCH_BAPM);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Dismiss the keyguard
                dismissKeyGuard();

                // Hide the fake loading screen.  This is used to keep this activity alive while dismissing the keyguard
                sendHomeAppTimer(3);
            }
        }, 1000);
    }

    //Dismiss the KeyGuard
    private void dismissKeyGuard(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Window window = getWindow();
            if (!BAPMPreferences.getKeepScreenON(this)) {
                ScreenONLock screenONLock = ScreenONLock.getInstance();
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
        boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(this);
        boolean launchPlayer = BAPMPreferences.getLaunchMusicPlayer(this);

        if(!launchMaps && !launchPlayer) {
            final Context context = this;
            int milliSeconds = seconds * 1000;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LaunchApp launchApp = new LaunchApp();
                    launchApp.sendEverythingToBackground(context);
                }
            };
            handler.postDelayed(runnable, milliSeconds);
        }
    }
}
