package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;

import maderski.bluetoothautoplaymusic.controls.VolumeControl;
import maderski.bluetoothautoplaymusic.helpers.PowerHelper;

/**
 * Created by Jason on 6/1/16.
 */
public class Telephone {

    private static final String TAG = Telephone.class.getName();

    private Context context;
    private TelephonyManager telephonyManager;

    public Telephone(Context context){
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public boolean isOnCall(){
        int currentCallState = telephonyManager.getCallState();

        if(currentCallState == TelephonyManager.CALL_STATE_OFFHOOK){
            Log.d(TAG, "ON CALL!");
            return true;
        }else{
            Log.d(TAG, "Not on Call");
            return false;
        }
    }

    public void CheckIfOnPhone(VolumeControl volumeControl){
        final VolumeControl vc = volumeControl;

        int _seconds = 7200000; //check for 2 hours
        int _interval = 2000; //2 second interval

        new CountDownTimer(_seconds, _interval)
        {
            public void onTick(long millisUntilFinished) {
                if(PowerHelper.isPluggedIn(context)){
                    if(isOnCall()){
                        Log.d(TAG, "On Call, check again in 3 sec");
                    }else{
                        Log.d(TAG, "Off Call, Launching Bluetooth Autoplay music");
                        cancel();
                        //Get Original Volume and Launch Bluetooth Autoplay Music
                        vc.delayGetOrigVol(3);
                    }
                }else{
                    //Bailing cause phone is not plugged in
                    Log.d(TAG, "Phone is no longer plugged in to power");
                    cancel();
                }
            }

            public void onFinish() {
                //does nothing currently
            }
        }.start();
    }
}
