package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 12/8/15.
 */
public class RingerControl {
    private AudioManager am;

    //initialize AudioManager
    public RingerControl(Context context){
        am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
    }
    //turns phone sounds OFF
    public void soundsOFF(){
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.i("RingerControl: ", "Silent");
    }
    //turns phone sounds ON
    public void soundsON(){
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.i("RingerControl: ", "Normal");
    }

}
