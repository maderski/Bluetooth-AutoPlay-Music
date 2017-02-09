package maderski.bluetoothautoplaymusic.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Receivers.BTStateChangedReceiver;
import maderski.bluetoothautoplaymusic.Receivers.BluetoothReceiver;
import maderski.bluetoothautoplaymusic.Receivers.CustomReceiver;
import maderski.bluetoothautoplaymusic.Receivers.PowerReceiver;
import maderski.bluetoothautoplaymusic.ScreenONLock;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 1/5/16.
 */
public class BAPMService extends Service {
    BluetoothReceiver mBluetoothReceiver;
    BTStateChangedReceiver mBTStateChangedReceiver;
    CustomReceiver mCustomReceiver;
    PowerReceiver mPowerReceiver;

    //Start the Bluetooth receiver as a service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(BuildConfig.DEBUG) {
            Log.d("BAPMService: ", "started");
            Toast.makeText(this, "BAPMService started", Toast.LENGTH_LONG).show();
        }

        // Register receivers
        mBluetoothReceiver = new BluetoothReceiver();
        mBluetoothReceiver.onReceive(this, intent);

        mBTStateChangedReceiver = new BTStateChangedReceiver();
        mBTStateChangedReceiver.onReceive(this, intent);

        mCustomReceiver = new CustomReceiver();
        mCustomReceiver.onReceive(this, intent);

        mPowerReceiver = new PowerReceiver();
        mPowerReceiver.onReceive(this, intent);

        // Rehold WakeLock due to Service Restart
        reHoldWakeLock();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBluetoothReceiver != null){
            unregisterReceiver(mBluetoothReceiver);
            mBluetoothReceiver = null;
        }

        if(mBTStateChangedReceiver != null){
            unregisterReceiver(mBTStateChangedReceiver);
            mBTStateChangedReceiver = null;
        }

        if(mCustomReceiver != null) {
            unregisterReceiver(mCustomReceiver);
            mCustomReceiver = null;
        }

        if(mPowerReceiver != null) {
            unregisterReceiver(mPowerReceiver);
            mPowerReceiver = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    private void reHoldWakeLock(){
        boolean shouldKeepScreenOn = BAPMPreferences.getKeepScreenON(this);

        if(shouldKeepScreenOn) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            boolean ranBAPM = BAPMDataPreferences.getRanActionsOnBtConnect(this);
            boolean isConnectedToBT = audioManager.isBluetoothA2dpOn();

            if (ranBAPM && isConnectedToBT) {
                ScreenONLock screenONLock = ScreenONLock.getInstance();
                screenONLock.releaseWakeLock();
                screenONLock.enableWakeLock(this);

                // Log rehold wakelock event
                FirebaseHelper firebaseHelper = new FirebaseHelper(this);
                firebaseHelper.wakelockRehold();
            }
        }
    }
}
