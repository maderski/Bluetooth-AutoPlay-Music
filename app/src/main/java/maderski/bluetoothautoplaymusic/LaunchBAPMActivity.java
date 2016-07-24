package maderski.bluetoothautoplaymusic;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class LaunchBAPMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_bapm);
        dismissKeyGuard(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    //Dismiss the KeyGuard
    private void dismissKeyGuard(Context context){

        if (!BAPMPreferences.getKeepScreenON(context)){
            ScreenONLock screenONLock = new ScreenONLock();
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
}
