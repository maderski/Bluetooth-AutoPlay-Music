package maderski.bluetoothautoplaymusic.bluetoothactions;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.controls.RingerControl;
import maderski.bluetoothautoplaymusic.controls.VolumeControl;
import maderski.bluetoothautoplaymusic.services.BTStateChangedService;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.utils.ServiceUtils;

/**
 * Created by Jason on 6/3/17.
 */

public class BTHeadphonesActions {
    private static final String TAG = "BTHeadphonesActions";

    private final Context mContext;
    private final PlayMusicControl mPlayMusicControl;
    private final VolumeControl mVolumeControl;
    private final AudioManager mAudioManager;

    public BTHeadphonesActions(Context context){
        mContext = context;

        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        mPlayMusicControl = new PlayMusicControl(context);
        mVolumeControl = new VolumeControl(context);
    }

    public void connectActionsWithDelay(){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            connectActions();
            }
        };
        handler.postDelayed(runnable, 4000);

        ServiceUtils.INSTANCE.startService(mContext, BTStateChangedService.class, BTStateChangedService.TAG);
    }

    public void connectActions() {
        // Get headphone preferred volume
        int preferredVolume = BAPMPreferences.INSTANCE.getHeadphonePreferredVolume(mContext);
        // Set headphone preferred volume
        mVolumeControl.setSpecifiedVolume(preferredVolume);
        // Start checking if music is playing
        mPlayMusicControl.checkIfPlaying(mContext, 8);
        Log.d(TAG, "HEADPHONE VOLUME SET TO:" + Integer.toString(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

        BAPMDataPreferences.INSTANCE.setIsHeadphonesDevice(mContext, true);
        if(BuildConfig.DEBUG)
            Toast.makeText(mContext, "Music Playing", Toast.LENGTH_SHORT).show();
    }

    public void disconnectActions(){
        mPlayMusicControl.pause();
        PlayMusicControl.Companion.cancelCheckIfPlaying();
        if(mAudioManager.isMusicActive()) {
            mPlayMusicControl.pause();
        }
        mVolumeControl.setToOriginalVolume(new RingerControl(mContext));

        BAPMDataPreferences.INSTANCE.setIsHeadphonesDevice(mContext, false);
        if(BuildConfig.DEBUG)
            Toast.makeText(mContext, "Music Paused", Toast.LENGTH_SHORT).show();

        ServiceUtils.INSTANCE.stopService(mContext, BTStateChangedService.class, BTStateChangedService.TAG);
    }
}
