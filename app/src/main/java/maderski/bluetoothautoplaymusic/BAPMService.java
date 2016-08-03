package maderski.bluetoothautoplaymusic;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Jason on 1/5/16.
 */
public class BAPMService extends Service {

    //Start the Bluetooth receiver as a service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(BuildConfig.DEBUG)
            Log.i("BAPMService: ", "started");
        BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
        bluetoothReceiver.onReceive(this, intent);

        CustomReceiver customReceiver = new CustomReceiver();
        customReceiver.onReceive(this, intent);

        PowerReceiver powerReceiver = new PowerReceiver();
        powerReceiver.onReceive(this, intent);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(BuildConfig.DEBUG)
            Log.d("BTAPMService: ", "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
