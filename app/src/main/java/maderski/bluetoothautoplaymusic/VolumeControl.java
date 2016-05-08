package maderski.bluetoothautoplaymusic;

import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Jason on 4/2/16.
 */
public class VolumeControl {

    private static String TAG = VolumeControl.class.getName();
    public static int originalMediaVolume;

    //Set Mediavolume to MAX
    public static void volumeMAX(){
        int maxVolume = VariableStore.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "Max Media Volume is: " + Integer.toString(maxVolume));
        VariableStore.am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
    }

    //Set media volume
    public static void setOriginalVolume(){
        VariableStore.am.setStreamVolume(AudioManager.STREAM_MUSIC, originalMediaVolume, 0);
        Log.i(TAG, "Media Volume is set to: " + Integer.toString(originalMediaVolume));
    }

    public static void checkSetMAXVol(int seconds, int seconds_interval){
        int _seconds = seconds * 1000;
        int _interval = seconds_interval * 1000;
        new CountDownTimer(_seconds, _interval)
        {
            boolean runme = false;
            public void onTick(long millisUntilFinished) {
                if(runme) {
                    if (VariableStore.isBTConnected) {
                        if (VariableStore.am.getStreamVolume(AudioManager.STREAM_MUSIC) !=
                                VariableStore.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                            int maxVolume = VariableStore.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            VariableStore.am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                            Log.i(TAG, "Set Volume To MAX");
                        } else if (VariableStore.am.getStreamVolume(AudioManager.STREAM_MUSIC) ==
                                VariableStore.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                            Log.i(TAG, "Volume is at MAX!");
                        }
                    }
                }
                runme = true;
            }

            public void onFinish() {
                if(VariableStore.isBTConnected) {
                    if (VariableStore.am.getStreamVolume(AudioManager.STREAM_MUSIC) ==
                            VariableStore.am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                        Log.i(TAG, "Volume is at MAX!");
                    } else {
                        Log.i(TAG, "Unable to to set Volume to MAX :(");
                    }
                }
                runme = false;
            }
        }.start();

    }
}
