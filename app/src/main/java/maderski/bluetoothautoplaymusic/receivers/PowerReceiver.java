package maderski.bluetoothautoplaymusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;

/**
 * Created by Jason on 7/23/16.
 */
public class PowerReceiver extends BroadcastReceiver {
    private static final String TAG = PowerReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        boolean isBTConnected = audioManager.isBluetoothA2dpOn();
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);
        boolean ranActionsOnBtConnect = BAPMDataPreferences.getRanActionsOnBtConnect(context);
        boolean isHeadphones = BAPMDataPreferences.getIsAHeadphonesDevice(context);

        Log.d(TAG, "Power Connected to a Selected BTDevice");
        Log.d(TAG, "Is BT Connected: " + Boolean.toString(isBTConnected));

        if (powerRequired && isBTConnected && !ranActionsOnBtConnect && !isHeadphones) {
            Intent launchIntent = new Intent();
            launchIntent.setAction("maderski.bluetoothautoplaymusic.pluggedinlaunch");
            context.sendBroadcast(launchIntent);
        }
    }

}
