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

    private AudioManager mAudioManager;
    private PlayerControls playerControls;
    private FirebaseHelper mFirebaseHelper;

    public PlayMusic(Context context){
        this(context, (AudioManager)context.getSystemService(Context.AUDIO_SERVICE));
    }

    public PlayMusic(Context context, AudioManager audioManager){
        this.mAudioManager = audioManager;
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

    public void pause(){ playerControls.pause(); }

    public void play(){ playerControls.play(); }

    public void checkIfPlaying(int seconds, int intervalSeconds){
        int milliseconds = seconds * 1000;
        int intervalMills = intervalSeconds * 1000;
        new CountDownTimer(milliseconds, intervalMills){
            @Override
            public void onTick(long millisUntilFinished) {
                if(BuildConfig.DEBUG){
                    Log.i(TAG, "millisUntilFinished: " + Long.toString(millisUntilFinished));
                    Log.i(TAG, "Is Music Active: " + Boolean.toString(mAudioManager.isMusicActive()));
                }

                if (mAudioManager.isMusicActive()) {
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

                if(!mAudioManager.isBluetoothA2dpOn()){
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "Bluetooth is not connected");
                    }
                    pause();
                    cancel();
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

