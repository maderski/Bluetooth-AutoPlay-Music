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

    public static boolean selectedBTDevice = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isBTConnected = am.isBluetoothA2dpOn();
        if(BuildConfig.DEBUG) {
            Log.i(TAG, "Power Connected");
            Log.i(TAG, "Is BT Connected: " + Boolean.toString(isBTConnected));
        }
        //Toast.makeText(context, "BAPM Power Connected", Toast.LENGTH_SHORT).show();
        boolean powerRequired = BAPMPreferences.getPowerConnected(context);

        if(powerRequired && isBTConnected && !BluetoothActions.getRanActionsOnBTConnect() && selectedBTDevice){
            //Toast.makeText(context, "BTAudioPWR Launch", Toast.LENGTH_SHORT).show();
            BluetoothActions bluetoothActions = new BluetoothActions(context, am);
            bluetoothActions.OnBTConnect();
        }
    }

}
