package maderski.bluetoothautoplaymusic.Services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Receivers.BluetoothReceiver;
import maderski.bluetoothautoplaymusic.Utils.ServiceRestartUtils;

/**
 * Created by Jason on 1/5/16.
 */
public class BAPMService extends Service {

    private final BluetoothReceiver mBluetoothReceiver = new BluetoothReceiver();

    //Start the Bluetooth receiver as a service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "started");
            Toast.makeText(this, "BAPMService started", Toast.LENGTH_LONG).show();
        }

        // Initalize and start Crashlytics
        Fabric.with(this, new Crashlytics());

        // Start Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        filter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        registerReceiver(mBluetoothReceiver, filter);

        // Rehold WakeLock due to Service Restart
        ServiceRestartUtils.reHoldWakeLock(this);

        // Restart additional services due to Service Restart
        ServiceRestartUtils.restartAdditionServices(this);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop Bluetooth Connected, Disconnected and A2DP Broadcast Receivers
        unregisterReceiver(mBluetoothReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
