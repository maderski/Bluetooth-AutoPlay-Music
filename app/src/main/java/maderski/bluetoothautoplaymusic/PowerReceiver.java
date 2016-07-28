package maderski.bluetoothautoplaymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 7/23/16.
 */
public class PowerReceiver extends BroadcastReceiver {
    private static final String TAG = PowerReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "BAPM Power Connected", Toast.LENGTH_SHORT).show();

        if(CustomReceiver.getIsSelectedDevice()) {
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            boolean isBTConnected = audioManager.isBluetoothA2dpOn();
            boolean powerRequired = BAPMPreferences.getPowerConnected(context);

            if(BuildConfig.DEBUG) {
                Log.i(TAG, "Power Connected to a Selected BTDevice");
                Log.i(TAG, "Is BT Connected: " + Boolean.toString(isBTConnected));
            }
            if (powerRequired && isBTConnected && !BluetoothActions.getRanActionsOnBTConnect()) {
                //Toast.makeText(context, "BTAudioPWR Launch", Toast.LENGTH_SHORT).show();
                Intent launchIntent = new Intent();
                launchIntent.setAction("maderski.bluetoothautoplaymusic.pluggedinlaunch");
                context.sendBroadcast(launchIntent);
            }
        }
    }

}
