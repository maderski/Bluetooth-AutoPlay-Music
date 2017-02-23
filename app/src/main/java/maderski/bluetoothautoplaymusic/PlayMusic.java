package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Receivers.BluetoothReceiver;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic {

    private static final String TAG = PlayMusic.class.getName();

    private PlayerControls playerControls;
    private FirebaseHelper mFirebaseHelper;

    private Context mContext;

    public PlayMusic(Context context){
        mContext = context;
        setPlayerControls();
        mFirebaseHelper = new FirebaseHelper(context);
    }

    private void setPlayerControls(){
        boolean launchMusicPlayer = BAPMPreferences.getLaunchMusicPlayer(mContext);
        if(!launchMusicPlayer){
            playerControls = new OtherMusicPlayer(mContext);
        }else {
            String pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(mContext);
            switch (pkgName) {
                case PackageTools.SPOTIFY:
                    playerControls = new Spotify(mContext);
                    break;
                case PackageTools.GOOGLEPLAYMUSIC:
                    playerControls = new GooglePlayMusic(mContext);
                    break;
                case PackageTools.BEYONDPOD:
                    playerControls = new BeyondPod(mContext);
                    break;
                default:
                    playerControls = new OtherMusicPlayer(mContext);
                    break;
            }
        }
    }

    public void pause(){ playerControls.pause(); }

    public void play(){
        Log.d(TAG, "Tried to play");
        playerControls.play();
    }

    public synchronized void checkIfPlaying(final Context context, final int seconds){
        long milliseconds = seconds * 1000;
        new CountDownTimer(milliseconds, 1000) {
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            @Override
            public void onTick(long l) {
                if(BuildConfig.DEBUG){
                    Log.d(TAG, "millsUntilFinished: " + Long.toString(l));
                    Log.d(TAG, "isMusicPlaying: " + Boolean.toString(audioManager.isMusicActive()));
                }

                if(audioManager.isMusicActive()){
                    Log.d(TAG, "Music is playing");
                }else{
                    play();
                }
            }

            @Override
            public void onFinish() {
                if(!audioManager.isMusicActive()){
                    pause();
                    play();
                }
                mFirebaseHelper.musicAutoPlay(audioManager.isMusicActive());
            }
        }.start();
    }
}

