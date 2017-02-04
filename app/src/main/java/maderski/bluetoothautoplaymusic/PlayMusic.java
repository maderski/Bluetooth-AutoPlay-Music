package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic {

    private static final String TAG = PlayMusic.class.getName();

    private AudioManager audioManager;
    private PlayerControls playerControls;
    private FirebaseHelper mFirebaseHelper;
    private CountDownTimer mCheckIfPlayingTimer;

    public PlayMusic(Context context, AudioManager audioManager){
        this.audioManager = audioManager;
        setPlayerControls(context, audioManager);
        mFirebaseHelper = new FirebaseHelper(context);
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

    public void pauseAndCancel(){
        playerControls.pause();
        if(mCheckIfPlayingTimer != null) {
            mCheckIfPlayingTimer.cancel();
        }
    }

    public void pause(){ playerControls.pause(); }

    public void play(){ playerControls.play(); }

    public void checkIfPlaying(int seconds){
        int milliseconds = seconds * 1000;
        final AudioManager am = audioManager;
        mCheckIfPlayingTimer = new CountDownTimer(milliseconds, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(BuildConfig.DEBUG){
                    Log.i(TAG, "millisUntilFinished: " + Long.toString(millisUntilFinished));
                    Log.i(TAG, "Is Music Active: " + Boolean.toString(audioManager.isMusicActive()));
                }

                if (am.isMusicActive()) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "Music is playing");
                    }
                    if(millisUntilFinished < 8000) {
                        mFirebaseHelper.musicAutoPlay(true);
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "checkIfPlaying cancelled");
                        }
                        cancel();
                    }
                } else {
                    play();
                }

                if(!am.isBluetoothA2dpOn()){
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "Bluetooth is not connected");
                    }
//                    pause();
//                    cancel();
                    pauseAndCancel();
                }
            }

            @Override
            public void onFinish() {
                pause();
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        play();
                    }
                };
                handler.postDelayed(runnable, 1000);
                mFirebaseHelper.musicAutoPlay(false);
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "Unable to Play :(");
                }
            }
        }.start();
    }
}

