package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 12/8/15.
 */
public class RingerControl {
    private static final String TAG = RingerControl.class.getName();
    private AudioManager am;

    public RingerControl(AudioManager audioManager){
        am = audioManager;
    }

    //turns phone sounds OFF & initialize AudioManager
    public void soundsOFF(){
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        if(BuildConfig.DEBUG)
            Log.i(TAG, "RingerControl: " + "Silent");
    }
    //turns phone sounds ON
    public void soundsON(){
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        if(BuildConfig.DEBUG)
            Log.i(TAG, "RingerControl: " + "Normal");
    }

    public void vibrateOnly(){
        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public int ringerSetting(){
        return am.getRingerMode();
    }
}
