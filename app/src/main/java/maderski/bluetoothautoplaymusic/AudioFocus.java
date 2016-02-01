package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 1/30/16.
 */
public class AudioFocus {

    private static final String TAG = AudioFocus.class.getName();

    public static void requestAudioFocus(Context context){
        AudioFocusListen afl = new AudioFocusListen();
        VariableStore.listener = afl.getAudioFocusChangeListener();
        VariableStore.am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int result = VariableStore.am.requestAudioFocus(VariableStore.listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            Log.i(TAG, "Audiofocus Request Granted");
            //Play music
            PlayMusic.play();
        }
    }

    public static void abandonAudioFocus(){
        int requestGranted = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        int abandonFocus = VariableStore.am.abandonAudioFocus(VariableStore.listener);
        if(requestGranted == abandonFocus){
            Log.i(TAG, "Audiofocus Abandoned");
        }
    }

    public static void getCurrentAudioFocus(Context context){
        AudioFocusListen afl = new AudioFocusListen();
        VariableStore.listener = afl.getAudioFocusChangeListener();
        VariableStore.am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }
}
