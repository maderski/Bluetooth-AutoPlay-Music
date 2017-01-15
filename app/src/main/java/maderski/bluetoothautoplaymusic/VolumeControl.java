package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Jason on 4/2/16.
 */
public class VolumeControl {

    private static final String TAG = VolumeControl.class.getName();

    private AudioManager am;

    public VolumeControl(AudioManager audioManager){
        am = audioManager;
    }

    //Set Mediavolume to MAX
    public void volumeMAX(){
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "Max Media Volume is: " + Integer.toString(maxVolume));
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
    }

    //Set original media volume
    public void setOriginalVolume(Context context){
        int originalMediaVolume = BAPMDataPreferences.getOriginalMediaVolume(context);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, originalMediaVolume, 0);

        if(BuildConfig.DEBUG)
            Log.i(TAG, "Media Volume is set to: " + Integer.toString(originalMediaVolume));
    }

    //Wait 3 seconds before getting the Original Volume and return true when done
    public void delayGetOrigVol(Context context, AudioManager audioManager){
        final Context ctx = context;
        final AudioManager am = audioManager;
        if(am.isBluetoothA2dpOn()) {
            new CountDownTimer(6000,
                    1000) {
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished > 3000 && millisUntilFinished < 4000) {
                        //Set original volume value in persistent data so it can be retrieved later
                        BAPMDataPreferences.setOriginalMediaVolume(ctx, am.getStreamVolume(AudioManager.STREAM_MUSIC));

                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(ctx)));
                    }
                }

                public void onFinish() {
                    Intent launchIntent = new Intent();
                    launchIntent.setAction("maderski.bluetoothautoplaymusic.offtelephonelaunch");
                    ctx.sendBroadcast(launchIntent);
                }
            }.start();
        }
    }

    public void checkSetMAXVol(final Context context, int seconds, int seconds_interval){
        int _seconds = seconds * 1000;
        int _interval = seconds_interval * 1000;
        final int maxVolume = BAPMPreferences.getUserSetMaxVolume(context);

        new CountDownTimer(_seconds, _interval)
        {
            boolean runme = false;
            public void onTick(long millisUntilFinished) {
                if(runme) {
                    if (am.isBluetoothA2dpOn()) {
                        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) != maxVolume) {
                                //am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                            //int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                            if(BuildConfig.DEBUG)
                                Log.i(TAG, "Set Volume To MAX");
                        } else if (am.getStreamVolume(AudioManager.STREAM_MUSIC) == maxVolume) {
                                //am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                            if(BuildConfig.DEBUG)
                                Log.i(TAG, "Volume is at MAX!");
                        }
                    }
                }
                runme = true;
            }

            public void onFinish() {
                if(am.isBluetoothA2dpOn()) {
                    if (am.getStreamVolume(AudioManager.STREAM_MUSIC) == maxVolume) {
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "Volume is at MAX!");
                    } else {
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "Unable to to set Volume to MAX :(");
                    }
                }
                runme = false;
            }
        }.start();
    }
}
