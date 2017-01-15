package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 1/14/17.
 */

public class BluetoothDeviceHelper {
    //List of bluetooth devices on the phone
    public static List<String> listOfBluetoothDevices(){
        List<String> btDevices = new ArrayList<String>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for(BluetoothDevice bt : pairedDevices)
                btDevices.add(bt.getName());
        }else{
            btDevices.add(0, "No Bluetooth Device found");
        }

        return btDevices;
    }
}
