package maderski.bluetoothautoplaymusic;

import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 1/30/16.
 */
public class AudioFocusListen{

    private static final String TAG = AudioFocusListen.class.getName();

    private boolean inAudioFocus = false;

    //AudioFocus Listener, provides audioFocus feedback
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange){
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.i(TAG, "AUDIOFOCUS_GAIN");
                    inAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIET_MAY_DUCK");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.e(TAG, "AUDIOFOCUS_LOSS");
                    inAudioFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;
            }

        }
    };

    //Not used
    public void setAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener audioFocusChangeListener) {
        this.audioFocusChangeListener = audioFocusChangeListener;
    }

    //Get AudioFocus Change Listener
    public AudioManager.OnAudioFocusChangeListener getAudioFocusChangeListener() {
        return audioFocusChangeListener;
    }
}
