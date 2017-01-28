package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic {

    private static final String TAG = PlayMusic.class.getName();

    private AudioManager audioManager;
    private PlayerControls playerControls;

    public PlayMusic(Context context, AudioManager audioManager){
        this.audioManager = audioManager;
        setPlayerControls(context, audioManager);
    }

    private void setPlayerControls(Context context, AudioManager audioManager){
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        if(!launchMusicPlayer){
            playerControls = new OtherMusicPlayer(audioManager);
        }else {
            String pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context);
            switch (pkgName) {
                case PackageTools.SPOTIFY:
                    playerControls = new Spotify(context, audioManager);
                    break;
                case PackageTools.GOOGLEPLAYMUSIC:
                    playerControls = new GooglePlayMusic(context, audioManager);
                    break;
                case PackageTools.BEYONDPOD:
                    playerControls = new BeyondPod(context, audioManager);
                    break;
                default:
                    playerControls = new OtherMusicPlayer(audioManager);
                    break;
            }
        }
    }

    public void pause(){ playerControls.pause(); }

    public void play(){ playerControls.play(); }

    public void checkIfPlaying(){
        final AudioManager am = audioManager;
        new CountDownTimer(13000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(BuildConfig.DEBUG){
                    Log.i(TAG, "millisUntilFinished: " + Long.toString(millisUntilFinished));
                    Log.i(TAG, "Is Music Active: " + Boolean.toString(audioManager.isMusicActive()));
                }

                if (am.isMusicActive()) {
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "Music is playing");
                    if(millisUntilFinished < 8000) {
                        if (BuildConfig.DEBUG)
                            Log.i(TAG, "checkIfPlaying cancelled");
                        cancel();
                    }
                } else {
                    play();
                }

                if(!am.isBluetoothA2dpOn()){
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "Bluetooth is not connected");
                    pause();
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Unable to Play :(");
            }
        }.start();
    }
}

