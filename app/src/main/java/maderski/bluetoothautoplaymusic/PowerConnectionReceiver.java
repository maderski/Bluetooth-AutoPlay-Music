package maderski.bluetoothautoplaymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Jason on 2/22/16.
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

    private final static String TAG = PowerConnectionReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Set<String> BTDeviceList = BAPMPreferences.getBTDevices(context);

        //Get action that was broadcasted
        String action = "None";

        try{
            action = intent.getAction();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        //Launches BTConnectPhoneDoStuff if phone is connected to Bluetooth and BTConnectPhoneDoStuff has not run
        if(action.equalsIgnoreCase(Intent.ACTION_POWER_CONNECTED) && BAPMPreferences.getBTDevices(context).contains(VariableStore.btDevice)){
            Log.i(TAG, "Power Connected");
            //Toast.makeText(context, "BAPM Power Connected", Toast.LENGTH_SHORT).show();
            boolean powerRequired = BAPMPreferences.getPowerConnected(context);

            if(powerRequired){
                if(VariableStore.isBTConnected && !VariableStore.ranBluetoothDoStuff){
                    //Toast.makeText(context, "BTAudioPWR Launch", Toast.LENGTH_SHORT).show();
                    BluetoothActions.BTConnectPhoneDoStuff(context, VariableStore.btDevice);
                }
            }
        }
    }
}
