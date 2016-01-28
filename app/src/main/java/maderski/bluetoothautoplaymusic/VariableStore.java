package maderski.bluetoothautoplaymusic;

import android.os.PowerManager;

/**
 * Created by Jason on 1/5/16.
 */
public class VariableStore {

    //Variables for wakelock
    public static PowerManager.WakeLock wakeLock;
    public static String stayOnTAG = "Stay ON";

    //Variable to Store Name of Launch Map App to display in toast message
    public static String toastMapApp;
}
