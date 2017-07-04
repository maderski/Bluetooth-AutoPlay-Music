package maderski.bluetoothautoplaymusic.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Controls.WakeLockControl.ScreenONLock;

/**
 * Created by Jason on 7/4/17.
 */

public class WakeLockService extends Service {
    public static final String TAG = "WakeLockService";
    
    private ScreenONLock mScreenONLock;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get wakelock instance
        mScreenONLock = ScreenONLock.getInstance();

        // Release wakelock if it is still held for some reason
        if(mScreenONLock.wakeLockHeld()){
            mScreenONLock.releaseWakeLock();
        }

        // Hold wakelock
        mScreenONLock.enableWakeLock(this);

        if(BuildConfig.DEBUG){
            Toast.makeText(this, "WAKELOCK HELD", Toast.LENGTH_LONG).show();
            Log.d(TAG, "WAKELOCK SERVICE STARTED");
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release wakelock
        if(mScreenONLock != null){
            if(mScreenONLock.wakeLockHeld()){
                mScreenONLock.releaseWakeLock();
                if(BuildConfig.DEBUG){
                    Toast.makeText(this, "WAKELOCK released", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "WAKELOCK SERVICE STOPPED");
                }
            }
        }
    }
}
