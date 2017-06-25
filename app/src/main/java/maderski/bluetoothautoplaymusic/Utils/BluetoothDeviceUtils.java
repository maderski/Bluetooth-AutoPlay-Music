package maderski.bluetoothautoplaymusic.Utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.v4.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 1/14/17.
 */

public class BluetoothDeviceUtils {
    private static final String TAG = "BluetoothDeviceUtils";

    // List of bluetooth devices on the phone
    public static List<String> listOfBluetoothDevices(){
        List<String> btDevices = new ArrayList<String>();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            for(BluetoothDevice bt : pairedDevices)
                btDevices.add(bt.getName());
        }else{
            btDevices.add(0, "No Bluetooth Device found");
        }

        return btDevices;
    }
}
