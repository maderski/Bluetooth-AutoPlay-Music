package maderski.bluetoothautoplaymusic;

import android.bluetooth.BluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Receivers.A2DPPlayingStateReceiver;
import maderski.bluetoothautoplaymusic.Receivers.BluetoothReceiver;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic implements A2DPPlayingStateReceiver.PlayingStateCallback {

    private static final String TAG = PlayMusic.class.getName();

    private PlayerControls playerControls;
    private FirebaseHelper mFirebaseHelper;
    private BroadcastReceiver mA2DPPlayingStateReceiver;

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

        mA2DPPlayingStateReceiver = new A2DPPlayingStateReceiver(this);
        IntentFilter intentFilter = new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        mContext.getApplicationContext().registerReceiver(mA2DPPlayingStateReceiver, intentFilter);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            @Override
            public void run() {
                if(!audioManager.isMusicActive()){
                    Log.d(TAG, "Music not active after " + Integer.toString(seconds));
                    pause();
                    play();
                }
                Log.d(TAG, "Is playing: " + Boolean.toString(audioManager.isMusicActive()));
                mFirebaseHelper.musicAutoPlay(audioManager.isMusicActive());
                if(mA2DPPlayingStateReceiver != null) {
                    mContext.getApplicationContext().unregisterReceiver(mA2DPPlayingStateReceiver);
                    mA2DPPlayingStateReceiver = null;
                }
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

    @Override
    public void playingState(boolean isPlaying) {
        if(!isPlaying) {
            play();
            Log.d(TAG, "SEND PLAY COMMAND");
        }
    }
}

