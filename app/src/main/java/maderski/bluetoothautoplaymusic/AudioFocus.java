package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 1/30/16.
 */
public class AudioFocus {

    private final String TAG = AudioFocus.class.getName();

    private AudioManager.OnAudioFocusChangeListener listener;
    public AudioManager am;

    public AudioFocus(Context context){
        AudioFocusListen afl = new AudioFocusListen();
        listener = afl.getAudioFocusChangeListener();
        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    //Request AudioFocus for Bluetooth autoplay music
    public void requestAudioFocus(Context context){
        int result = am.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        //Bluetooth autoplay music now has audioFocus
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            Log.i(TAG, "Audiofocus Request Granted");
            //Play music or a sound
        }
    }

    //Abandon AudioFocus
    public void abandonAudioFocus(){
        int requestGranted = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        int abandonFocus = am.abandonAudioFocus(listener);
        if(requestGranted == abandonFocus){
            Log.i(TAG, "Audiofocus Abandoned");
        }
    }
}
