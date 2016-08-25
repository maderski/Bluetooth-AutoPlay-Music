package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 1/5/16.
 */
public class VariousLists {
    private static final String TAG = VariousLists.class.getName();

    private VariousLists(){}

    //Returns true if a connected device on the connected device list is on the BAPMPreferences.
    //getBTDevices List that is set by the user in the UI
    public static boolean isADeviceOnBAPMList(Context context, List<String> connectedBTDeviceList){
        Set<String> userBTDeviceList = BAPMPreferences.getBTDevices(context);

        if(connectedBTDeviceList != null){
            return !Collections.disjoint(userBTDeviceList, connectedBTDeviceList);
        }else{
            if(BuildConfig.DEBUG)
                Log.i(TAG, "ConnectedBTDevices List = null");
        }
        return false;
    }

    //Generate a Test List
    private static List<String> generateTestList(String name, int number){
        List<String> test = new ArrayList<String>();
        int i = 0;
        while(i < number){
            name = name + Integer.toString(i);
            test.add(name);
            i++;
        }
        return test;
    }
}
