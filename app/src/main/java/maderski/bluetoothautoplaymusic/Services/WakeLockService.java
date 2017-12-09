package maderski.bluetoothautoplaymusic.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Controls.WakeLockControl.ScreenONLock;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.UI.activities.MainActivity;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

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

    @Override
    public void onCreate() {
        super.onCreate();
        String title = getString(R.string.wakelock_messge);
        String message = getString(R.string.app_name);

//        NotificationChannel channel = null;

//        if(Build.VERSION.SDK_INT > 25) {
//            String channelId = "BTAPMChannelID";
//            CharSequence channelName = "Bluetooth Autoplay Music";
//            channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
//        }

        ServiceUtils.createServiceNotification(3453, title, message, this, null);
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

        stopForeground(true);
    }
}
