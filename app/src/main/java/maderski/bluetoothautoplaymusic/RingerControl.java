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

    //Get original media volume
    public int getOriginalVolume(){
        int originalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "Original Media Volume is: " + Integer.toString(originalVolume));
        return originalVolume;
    }

    //Set media volume
    public void setVolume(int volume){
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        Log.i(TAG, "Media Volume is set to: " + Integer.toString(volume));
    }

    public void vibrateOnly(){
        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public int ringerSetting(){
        return am.getRingerMode();
    }
}
