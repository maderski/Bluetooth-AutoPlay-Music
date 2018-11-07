package maderski.bluetoothautoplaymusic.controls

import android.content.Context
import android.net.wifi.WifiManager
import android.widget.Toast
import maderski.bluetoothautoplaymusic.R

/**
 * Created by Jason on 2/26/17.
 */

object WifiControl {
    fun wifiON(context: Context, enable: Boolean) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = enable
        val toastMessage = if (enable) context.getString(R.string.wifi_turned_on) else context.getString(R.string.wifi_turned_off)
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

    fun isWifiON(context: Context): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }
}
