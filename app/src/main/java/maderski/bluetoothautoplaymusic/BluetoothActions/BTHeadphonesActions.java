package maderski.bluetoothautoplaymusic.BluetoothActions;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Controls.PlayMusicControl;
import maderski.bluetoothautoplaymusic.Controls.RingerControl;
import maderski.bluetoothautoplaymusic.Controls.VolumeControl;
import maderski.bluetoothautoplaymusic.Services.BTStateChangedService;
import maderski.bluetoothautoplaymusic.Services.OnBTConnectService;
import maderski.bluetoothautoplaymusic.Utils.ReceiverUtils;
import maderski.bluetoothautoplaymusic.Receivers.BTStateChangedReceiver;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

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

    public void connectActions(){
        mVolumeControl.saveOriginalVolume();
        Log.d(TAG, "Original Volume: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(mContext)));
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Get headphone preferred volume
                int preferredVolume = BAPMPreferences.getHeadphonePreferredVolume(mContext);
                // Set headphone preferred volume
                mVolumeControl.setSpecifiedVolume(preferredVolume);
                // Play music
                mPlayMusicControl.play();
                // Start checking if music is playing
                mPlayMusicControl.checkIfPlaying(mContext, 5);
                Log.d(TAG, "HEADPHONE VOLUME SET TO:" + Integer.toString(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));

                BAPMDataPreferences.setIsHeadphonesDevice(mContext, true);
                if(BuildConfig.DEBUG)
                    Toast.makeText(mContext, "Music Playing", Toast.LENGTH_SHORT).show();
            }
        };
        handler.postDelayed(runnable, 6000);

        ServiceUtils.startService(mContext, BTStateChangedService.class, BTStateChangedService.TAG);
    }

    public void disconnectActions(){
        mPlayMusicControl.pause();
        PlayMusicControl.cancelCheckIfPlaying();
        if(mAudioManager.isMusicActive()) {
            mPlayMusicControl.pause();
        }
        mVolumeControl.setToOriginalVolume(new RingerControl(mContext));

        BAPMDataPreferences.setIsHeadphonesDevice(mContext, false);
        if(BuildConfig.DEBUG)
            Toast.makeText(mContext, "Music Paused", Toast.LENGTH_SHORT).show();

        ServiceUtils.stopService(mContext, BTStateChangedService.class, BTStateChangedService.TAG);
    }
}
