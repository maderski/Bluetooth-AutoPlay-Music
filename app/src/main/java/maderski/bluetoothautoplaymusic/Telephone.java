package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;

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

        if(currentCallState == telephonyManager.CALL_STATE_OFFHOOK){
            if(BuildConfig.DEBUG)
                Log.i(TAG, "ON CALL!");
            return true;
        }else{
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Not on Call");
            return false;
        }
    }

    public void CheckIfOnPhone(AudioManager audioManager, VolumeControl volumeControl){
        final Context ctx = context;
        final AudioManager am = audioManager;
        final VolumeControl vc = volumeControl;

        int _seconds = 7200000; //check for 2 hours
        int _interval = 3000; //3 second interval

        new CountDownTimer(_seconds, _interval)
        {
            public void onTick(long millisUntilFinished) {
                if(Power.isPluggedIn(ctx)){
                    if(isOnCall()){
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "On Call, check again in 3 sec");
                    }else{
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "Off Call, Launching Bluetooth Autoplay music");
                        cancel();
                        //Get Original Volume and Launch Bluetooth Autoplay Music
                        vc.delayGetOrigVol(ctx, am);
                    }
                }else{
                    //Bailing cause phone is not plugged in
                    if(BuildConfig.DEBUG)
                        Log.i(TAG, "Phone is no longer plugged in to power");
                    cancel();
                }
            }

            public void onFinish() {
                //does nothing currently
            }
        }.start();
    }
}
