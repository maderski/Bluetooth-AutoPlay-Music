package maderski.bluetoothautoplaymusic.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.Helpers.PowerHelper;

/**
 * Created by Jason on 7/23/16.
 */
public class PowerReceiver extends BroadcastReceiver {
    private static final String TAG = PowerReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "BAPM Power Connected", Toast.LENGTH_SHORT).show();

        if(BAPMDataPreferences.getIsSelected(context) && PowerHelper.isPluggedIn(context)) {
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            boolean isBTConnected = audioManager.isBluetoothA2dpOn();
            boolean powerRequired = BAPMPreferences.getPowerConnected(context);
            boolean ranActionsOnBtConnect = BAPMDataPreferences.getRanActionsOnBtConnect(context);
            boolean isHeadphones = BAPMDataPreferences.getIsAHeadphonesDevice(context);

            Log.d(TAG, "Power Connected to a Selected BTDevice");
            Log.d(TAG, "Is BT Connected: " + Boolean.toString(isBTConnected));

            if (powerRequired && isBTConnected && !ranActionsOnBtConnect && !isHeadphones) {
                //Toast.makeText(context, "BTAudioPWR Launch", Toast.LENGTH_SHORT).show();
                Intent launchIntent = new Intent();
                launchIntent.setAction("maderski.bluetoothautoplaymusic.pluggedinlaunch");
                context.sendBroadcast(launchIntent);
            }
        }
    }

}
