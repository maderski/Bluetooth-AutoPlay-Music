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

    public RingerControl(Context context){
        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    //turns phone sounds OFF & initialize AudioManager
    public void soundsOFF(){
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.d(TAG, "RingerControl: " + "Silent");
    }
    //turns phone sounds ON
    public void soundsON(){
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.d(TAG, "RingerControl: " + "Normal");
    }

    public void vibrateOnly(){
        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public int ringerSetting(){
        return am.getRingerMode();
    }
}
