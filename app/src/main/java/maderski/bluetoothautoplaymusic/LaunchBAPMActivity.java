package maderski.bluetoothautoplaymusic;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class LaunchBAPMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_bapm);
        dismissKeyGuard(this);
        sendHomeAppTimer(3);
    }

    //Dismiss the KeyGuard
    private void dismissKeyGuard(Context context){

        if (!BAPMPreferences.getKeepScreenON(context)){
            ScreenONLock screenONLock = ScreenONLock.getInstance();
            screenONLock.enableWakeLock(context);
            Window window = ((Activity) context).getWindow();
            //window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            screenONLock.releaseWakeLock();
        }else{
            Window window = ((Activity) context).getWindow();
            //window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void sendHomeAppTimer(int seconds){
        boolean launchMaps = BAPMPreferences.getLaunchGoogleMaps(this);
        boolean launchPlayer = BAPMPreferences.getLaunchMusicPlayer(this);

        if(!launchMaps && !launchPlayer) {
            final Context context = this;
            int milliSeconds = seconds * 1000;

            new CountDownTimer(milliSeconds, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    LaunchApp launchApp = new LaunchApp(context);
                    launchApp.sendEverythingToBackground();
                }
            }.start();
        }
    }
}
