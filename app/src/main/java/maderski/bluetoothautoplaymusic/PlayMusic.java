package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Receivers.A2DPPlayingStateReceiver;
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

    public synchronized void checkIfPlaying(final int seconds){
        long milliseconds = seconds * 1000;
        final AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(!audioManager.isMusicActive()){
                    Log.d(TAG, "Music not active after " + Integer.toString(seconds));
                    pause();
                    play();
                }
                Log.d(TAG, "Is playing: " + Boolean.toString(audioManager.isMusicActive()));
                mFirebaseHelper.musicAutoPlay(audioManager.isMusicActive());
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }
}

