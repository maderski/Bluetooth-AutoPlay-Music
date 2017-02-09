package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 4/2/16.
 */
public class VolumeControl {

    private static final String TAG = VolumeControl.class.getName();

    private AudioManager am;

    public VolumeControl(Context context){
        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    //Set Mediavolume to MAX
    public void volumeMAX(){
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "Max Media Volume is: " + Integer.toString(maxVolume));
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
    }

    //Set original media volume
    public void setOriginalVolume(Context context){
        int originalMediaVolume = BAPMDataPreferences.getOriginalMediaVolume(context);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, originalMediaVolume, 0);

        Log.d(TAG, "Media Volume is set to: " + Integer.toString(originalMediaVolume));
    }

    //Wait 3 seconds before getting the Original Volume and return true when done
    public void delayGetOrigVol(final Context context, int seconds){
        int milliseconds = seconds * 1000;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BAPMDataPreferences.setOriginalMediaVolume(context, am.getStreamVolume(AudioManager.STREAM_MUSIC));

                Log.d(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(context)));

                Intent launchIntent = new Intent();
                launchIntent.setAction("maderski.bluetoothautoplaymusic.offtelephonelaunch");
                context.sendBroadcast(launchIntent);
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    private void setMaxVol(Context context){
        final int maxVolume = BAPMPreferences.getUserSetMaxVolume(context);
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) != maxVolume) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            Log.d(TAG, "Set Volume To MAX");
        } else if (am.getStreamVolume(AudioManager.STREAM_MUSIC) == maxVolume) {
            Log.d(TAG, "Volume is at MAX!");
        }
    }

    public void checkSetMAXVol(final Context context, int seconds){
        int milliseconds = seconds * 1000;

        setMaxVol(context);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setMaxVol(context);
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }
}
