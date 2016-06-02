package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Jason on 6/1/16.
 */
public class Telephone {

    private final String TAG = Telephone.class.getName();

    private TelephonyManager telephonyManager;

    public Telephone(Context context){
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public boolean isOnCall(){
        int currentCallState = telephonyManager.getCallState();

        if(currentCallState == telephonyManager.CALL_STATE_OFFHOOK){
            Log.i(TAG, "ON CALL!");
            return true;
        }else{
            Log.i(TAG, "Not on Call");
            return false;
        }
    }
}
