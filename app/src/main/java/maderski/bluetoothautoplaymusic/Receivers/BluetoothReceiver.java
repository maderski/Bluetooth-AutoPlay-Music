package maderski.bluetoothautoplaymusic.Receivers;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.BluetoothActions.BTHeadphonesActions;
import maderski.bluetoothautoplaymusic.Helpers.BluetoothConnectHelper;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 1/5/16.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothReceiver";

    //On receive of Broadcast
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (btDevice != null && intent.getAction() != null) {
                final String action = intent.getAction();
                Log.d(TAG, "ACTION: " + action);

                final String btDeviceName = btDevice.getName() != null ? btDevice.getName() : "None";

                boolean isASelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(btDeviceName);
                boolean isAHeadphonesBTDevice = BAPMPreferences.getHeadphoneDevices(context).contains(btDeviceName);
                Log.d(TAG, "Device: " + btDeviceName +
                        "\nis SelectedBTDevice: " + Boolean.toString(isASelectedBTDevice) +
                        "\nis A Headphone device: " + Boolean.toString(isAHeadphonesBTDevice));

                if(isASelectedBTDevice || isAHeadphonesBTDevice) {
                    if(isAHeadphonesBTDevice){
                        BTHeadphonesActions btHeadphonesActions = new BTHeadphonesActions(context);
                        switch (action) {
                            case BluetoothDevice.ACTION_ACL_CONNECTED:
                                btHeadphonesActions.connectActions();
                                break;
                            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                                btHeadphonesActions.disconnectActions();
                                break;
                        }
                    } else if(action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)){
                        final BluetoothConnectHelper bluetoothConnectHelper =
                                new BluetoothConnectHelper(context, btDeviceName);

                        final int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);
                        bluetoothConnectHelper.a2dpActions(state);
                    }
                }
            }
        }
    }
}
