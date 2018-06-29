package maderski.bluetoothautoplaymusic.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import maderski.bluetoothautoplaymusic.R;

/**
 * Created by Jason on 1/14/17.
 */

public class BluetoothDeviceUtils {
    public static final String TAG = "BluetoothDeviceUtils";

    // List of bluetooth devices on the phone
    public static List<String> listOfBluetoothDevices(Context context) {
        List<String> btDevices = new ArrayList<String>();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            for(BluetoothDevice bt : pairedDevices)
                btDevices.add(bt.getName());
        }else{
            btDevices.add(0, context.getString(R.string.no_bluetooth_device));
        }

        return btDevices;
    }
}
