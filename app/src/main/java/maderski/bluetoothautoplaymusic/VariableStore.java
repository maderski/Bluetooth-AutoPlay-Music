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

    //Variable for AudioFocus
    public static AudioManager am;
    public static AudioManager.OnAudioFocusChangeListener listener;
    public static boolean inAudioFocus;

    //Variable for Bluetooth connection status
    public static boolean isBTConnected = false;

    //Variable has BTConnectPhoneDoStuff ran
    public static boolean ranBTConnectPhoneDoStuff = false;
}
