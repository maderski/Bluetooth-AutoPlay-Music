package maderski.bluetoothautoplaymusic.Helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.v4.util.ArraySet;
import android.util.Log;

import java.util.List;
import java.util.Set;

import maderski.bluetoothautoplaymusic.Utils.BluetoothDeviceUtils;

/**
 * Created by Jason on 6/25/17.
 */

public class A2DPHelper implements BluetoothProfile.ServiceListener{
    private static final String TAG = "A2DPHelper";

    public interface A2DPCallbacks {
        void connectedDeviceNames(Set<String> deviceNames);
    }

    private A2DPCallbacks mCallbacks;

    public A2DPHelper(A2DPCallbacks callbacks){
        mCallbacks = callbacks;
    }

    // Get List of currently connected bluetooth devices
    public void getConnectedA2DPDevices(Context context){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.getProfileProxy(context, this, BluetoothProfile.A2DP);
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        Set<String> deviceNames = new ArraySet<>();
        List<BluetoothDevice> devices = proxy.getConnectedDevices();
        for(BluetoothDevice device : devices){
            Log.d(TAG, "CONNECTED DEVICE: " + device.getName());
            deviceNames.add(device.getName());
        }
        mCallbacks.connectedDeviceNames(deviceNames);
    }

    @Override
    public void onServiceDisconnected(int profile) {

    }
}
