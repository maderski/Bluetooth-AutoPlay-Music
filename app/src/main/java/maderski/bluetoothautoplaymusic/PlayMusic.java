package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
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

    private PlayerControls playerControls;
    private FirebaseHelper mFirebaseHelper;

    public PlayMusic(Context context){
        setPlayerControls(context);
        mFirebaseHelper = new FirebaseHelper(context);
    }

    private void setPlayerControls(Context context){
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(context);
        if(!launchMusicPlayer){
            playerControls = new OtherMusicPlayer(context);
        }else {
            String pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context);
            switch (pkgName) {
                case PackageTools.SPOTIFY:
                    playerControls = new Spotify(context);
                    break;
                case PackageTools.GOOGLEPLAYMUSIC:
                    playerControls = new GooglePlayMusic(context);
                    break;
                case PackageTools.BEYONDPOD:
                    playerControls = new BeyondPod(context);
                    break;
                default:
                    playerControls = new OtherMusicPlayer(context);
                    break;
            }
        }
    }

    public void pause(){ playerControls.pause(); }

    public void play(){ playerControls.play(); }

    public void checkIPlaying(Context context, int delaySeconds) {
        long milliseconds = delaySeconds * 1000;
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(!audioManager.isMusicActive()){
                    pause();
                    play();
                }
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

//    public void checkIfPlaying(int seconds, int intervalSeconds){
//        int milliseconds = seconds * 1000;
//        int intervalMills = intervalSeconds * 1000;
//        new CountDownTimer(milliseconds, intervalMills){
//            @Override
//            public void onTick(long millisUntilFinished) {
//                if(BuildConfig.DEBUG){
//                    Log.i(TAG, "millisUntilFinished: " + Long.toString(millisUntilFinished));
//                    Log.i(TAG, "Is Music Active: " + Boolean.toString(mAudioManager.isMusicActive()));
//                }
//
//                if (mAudioManager.isMusicActive()) {
//                    if (BuildConfig.DEBUG) {
//                        Log.i(TAG, "Music is playing");
//                    }
//                    if(millisUntilFinished < 8000) {
//                        mFirebaseHelper.musicAutoPlay(true);
//                        if (BuildConfig.DEBUG) {
//                            Log.i(TAG, "checkIfPlaying cancelled");
//                        }
//                        cancel();
//                    }
//                } else {
//                    play();
//                }
//
//                if(!mAudioManager.isBluetoothA2dpOn()){
//                    if (BuildConfig.DEBUG) {
//                        Log.i(TAG, "Bluetooth is not connected");
//                    }
//                    pause();
//                    cancel();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                pause();
//                Handler handler = new Handler();
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        play();
//                    }
//                };
//                handler.postDelayed(runnable, 1000);
//                mFirebaseHelper.musicAutoPlay(false);
//                if(BuildConfig.DEBUG) {
//                    Log.i(TAG, "Unable to Play :(");
//                }
//            }
//        }.start();
//    }
}

