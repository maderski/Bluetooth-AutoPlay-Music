package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 12/8/15.
 */
public class RingerControl {
    private String TAG = RingerControl.class.getName();
    private AudioManager am;

    public RingerControl(Context context){
        am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
    }

    //turns phone sounds OFF & initialize AudioManager
    public void soundsOFF(){
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.i(TAG, "RingerControl: " + "Silent");
    }
    //turns phone sounds ON
    public void soundsON(){
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.i(TAG, "RingerControl: " + "Normal");
    }

    //Set Mediavolume to MAX & initialize AudioManager
    public void volumeMAX(){
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
        Log.i(TAG, "Max Media Volume Set");
    }

    public void vibrateOnly(){
        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public int ringerSetting(){
        return am.getRingerMode();
    }
}
