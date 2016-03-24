package maderski.bluetoothautoplaymusic;

import android.media.AudioManager;
import android.os.PowerManager;

/**
 * Created by Jason on 1/5/16.
 */
public class VariableStore {

    //Variables for wakelock
    public static PowerManager.WakeLock wakeLock;
    public static String stayOnTAG = "Stay ON";

    //For Ringer Control
    public static RingerControl ringerControl;
    //public static int currentRingerSet;

    //Variable to Store Name of Launch Map App to display in toast message
    public static String toastMapApp;

    //Variable for AudioFocus
    public static AudioManager am;
    public static AudioManager.OnAudioFocusChangeListener listener;
    public static boolean inAudioFocus;

    //Variable for Bluetooth connection status
    public static boolean isBTConnected = false;

    //Variable to store Bluetooth device
    public static String btDevice;

    //Variable has BTConnectPhoneDoStuff ran
    public static boolean ranBTConnectPhoneDoStuff = false;
}
