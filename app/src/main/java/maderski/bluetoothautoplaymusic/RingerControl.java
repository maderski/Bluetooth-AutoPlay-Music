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


    //turns phone sounds OFF & initialize AudioManager
    public void soundsOFF(Context context){
        am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.i(TAG, "RingerControl: " + "Silent");
    }
    //turns phone sounds ON
    public void soundsON(Context context){
        am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.i(TAG, "RingerControl: " + "Normal");
    }

    //Set Mediavolume to MAX & initialize AudioManager
    public void volumeMAX(Context context){
        am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
        Log.i(TAG, "Max Media Volume Set");
    }

}
