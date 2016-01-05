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
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 1/5/16.
 */
public class VariousLists {
    private static String TAG = VariousLists.class.getName();

    //List of Mediaplayers that is installed on the phone
    public static List<String> listOfInstalledMediaPlayers(Context context){
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        List<String> installedMediaPlayers = new ArrayList<>();

        for(ResolveInfo ri:pkgAppsList){
            String resolveInfo = ri.toString();
            //Log.i("resolve ", resolveInfo);
            if(resolveInfo.contains("pandora")
                    || resolveInfo.contains(".playback")
                    || resolveInfo.contains("music")
                    || resolveInfo.contains("Music")) {
                String[] resolveInfoSplit = resolveInfo.split(" ");
                String pkg = resolveInfoSplit[1].substring(0, resolveInfoSplit[1].indexOf("/"));
                if (!installedMediaPlayers.contains(pkg)) {
                    installedMediaPlayers.add(pkg);
                }
            }
        }
        return installedMediaPlayers;
    }

    //List of bluetooth devices on the phone
    public static List<String> listOfBluetoothDevices(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> btDevices = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            btDevices.add(bt.getName());

        return btDevices;
    }

    //List of Installed Packages on the phone
    public static void listOfPackagesOnPhone(Context context){
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo pkg:appInfo){
            Log.i(TAG, "Installed Pkg: " + pkg.packageName);
        }


    }

}
