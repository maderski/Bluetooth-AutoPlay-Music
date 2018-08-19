package maderski.bluetoothautoplaymusic.services;

import android.app.NotificationChannel;
import android.app.Service;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import maderski.bluetoothautoplaymusic.BAPMNotification;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.receivers.BluetoothReceiver;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

/**
 * Created by Jason on 1/5/16.
 */
public class BAPMService extends Service {
    public static final String TAG = "BAPMService";

    private final BluetoothReceiver mBluetoothReceiver = new BluetoothReceiver();

    //Start the Bluetooth receiver as a service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "started");
            Toast.makeText(this, "BAPMService started", Toast.LENGTH_LONG).show();
        }

        // Initalize and start Crashlytics
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        // Start Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        IntentFilter filter = new IntentFilter();
        registerReceiver(mBluetoothReceiver, filter);

        // Cancel the JobScheduler that was used to start the BTAPMService
        JobScheduler jobScheduler = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
            Log.d(TAG, "BAPM JobService cancelled");
        }

        // Bring service out of the foreground state
        stopForeground(true);

        // Stop the service from running
        stopSelf();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String title = getString(R.string.initializing);
        String message = getString(R.string.app_name);
        ServiceUtils.createServiceNotification(3455,
                title,
                message,
                this,
                ServiceUtils.CHANNEL_ID_FOREGROUND_SERVICE,
                ServiceUtils.CHANNEL_NAME_FOREGROUND_SERVICE,
                R.drawable.ic_notif_icon);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "stopped");
            Toast.makeText(this, "BAPMService stopped", Toast.LENGTH_LONG).show();
        }

        // Stop Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        unregisterReceiver(mBluetoothReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
