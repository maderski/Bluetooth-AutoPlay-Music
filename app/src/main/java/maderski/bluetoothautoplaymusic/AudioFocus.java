package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Jason on 1/30/16.
 */
public class AudioFocus {

    private static final String TAG = AudioFocus.class.getName();

    private static AudioFocusListen afl = new AudioFocusListen();

    public static void requestAudioFocus(Context context, String pkg){
        VariableStore.listener = afl.getAudioFocusChangeListener();
        VariableStore.am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int result = VariableStore.am.requestAudioFocus(VariableStore.listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            //Play music
            if(pkg.equalsIgnoreCase(ConstantStore.GOOGLEPLAYMUSIC)){
                PlayMusic.play_googlePlayMusic(context);
            }else if(pkg.equalsIgnoreCase(ConstantStore.SPOTIFY)){
                PlayMusic.play_spotify(context);
            }else if(pkg.equalsIgnoreCase(ConstantStore.PANDORA)){
                Log.i(TAG, "Play Pandora, but I got to figure this one out");
            }
        }
    }

    public static void abandonAudioFocus(){
        VariableStore.am.abandonAudioFocus(VariableStore.listener);
    }
    /*


    public boolean requestFocus(Context context, AudioManager.OnAudioFocusChangeListener listener){
        VariableStore.am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int requestGranted = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        int requestFocus = VariableStore.am.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        boolean result = requestGranted == requestFocus;
        Log.i(TAG, "Request Focus: " + result);
        return result;
    }

    public boolean abandonFocus(AudioManager.OnAudioFocusChangeListener listener){
        int requestGranted = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        int abandonFocus = VariableStore.am.abandonAudioFocus(listener);
        boolean result = requestGranted == abandonFocus;
        Log.i(TAG, "Abandon Focus: " + result);
        return  result;
    }
    */

}
