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

    //Feedback for if ranBTConnectPhoneDoStuff ran
    private static boolean ranBTConnectPhoneDoStuff;

    public static boolean getRanBTConnectPhoneDoStuff(){
        return ranBTConnectPhoneDoStuff;
    }
    public static void setRanBTConnectPhoneDoStuff(boolean didItRun){
        ranBTConnectPhoneDoStuff = didItRun;
    }
}
