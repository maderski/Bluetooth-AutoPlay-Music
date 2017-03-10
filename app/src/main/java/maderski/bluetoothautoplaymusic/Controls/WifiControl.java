package maderski.bluetoothautoplaymusic.Controls;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by Jason on 2/26/17.
 */

public class WifiControl {

    public static void wifiON(Context context, boolean enable) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enable);
        String toastMessage = enable ? "Wifi turned ON" : "Wifi turned OFF";
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
