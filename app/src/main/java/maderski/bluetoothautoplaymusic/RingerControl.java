package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 12/8/15.
 */
public class RingerControl {
    private String TAG = RingerControl.class.getName();

    //turns phone sounds OFF & initialize AudioManager
    public void soundsOFF(){
        VariableStore.am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.i(TAG, "RingerControl: " + "Silent");
    }
    //turns phone sounds ON
    public void soundsON(){
        VariableStore.am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.i(TAG, "RingerControl: " + "Normal");
    }

    //Set Mediavolume to MAX
    public void volumeMAX(){
        int maxVolume = VariableStore.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        VariableStore.am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        Log.i(TAG, "Max Media Volume Set " + Integer.toString(maxVolume));
        //if volume did not get set to MAX than increase it to MAX
        increaseVolumeToMAX();
    }

    //Set media volume
    public void setOriginalVolume(){
        VariableStore.am.setStreamVolume(AudioManager.STREAM_MUSIC, VariableStore.originalMediaVolume, 0);
        Log.i(TAG, "Media Volume is set to: " + Integer.toString(VariableStore.originalMediaVolume));
    }

    private void increaseVolumeToMAX(){
        int maxVolume = VariableStore.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = VariableStore.am.getStreamVolume(AudioManager.STREAM_MUSIC);

        while(maxVolume > currentVolume){
            VariableStore.am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
            currentVolume = VariableStore.am.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.i(TAG, "Media Volume increased:" + Integer.toString(currentVolume));
        }
    }

    public void vibrateOnly(){
        VariableStore.am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public int ringerSetting(){
        return VariableStore.am.getRingerMode();
    }
}
