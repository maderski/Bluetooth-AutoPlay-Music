package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import maderski.bluetoothautoplaymusic.BluetoothActions.BTHeadphonesActions;
import maderski.bluetoothautoplaymusic.Helpers.BluetoothLaunchHelper;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothReceiver.class.getName();

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (btDevice != null && intent.getAction() != null) {
                final int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);

                final String action = intent.getAction();
                Log.d(TAG, "ACTION: " + action);

                boolean isASelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(btDevice.getName());
                boolean isAHeadphonesBTDevice = BAPMPreferences.getHeadphoneDevices(context).contains(btDevice.getName());
                BAPMDataPreferences.setIsSelected(context, isASelectedBTDevice);
                Log.d(TAG, "Device: " + btDevice.getName() +
                        "\nis SelectedBTDevice: " + Boolean.toString(isASelectedBTDevice) +
                        "\nis A Headphone device: " + Boolean.toString(isAHeadphonesBTDevice));

                if(isASelectedBTDevice || isAHeadphonesBTDevice) {
                    AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

                    if(isAHeadphonesBTDevice){
                        BTHeadphonesActions btHeadphonesActions = new BTHeadphonesActions(context, audioManager);
                        switch (action) {
                            case BluetoothDevice.ACTION_ACL_CONNECTED:
                                btHeadphonesActions.connectActions();
                                break;
                            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                                btHeadphonesActions.disconnectActions();
                                break;
                        }
                    } else if(action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)){
                        BluetoothLaunchHelper bluetoothLaunchHelper =
                                new BluetoothLaunchHelper(context,
                                        btDevice, state);
                        bluetoothLaunchHelper.a2dpAction();
                    }
                }

            }
        }
    }
}
