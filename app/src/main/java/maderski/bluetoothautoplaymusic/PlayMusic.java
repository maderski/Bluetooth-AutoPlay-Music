package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Interfaces.A2DPPlayingState;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic implements A2DPPlayingState {

    private static final String TAG = PlayMusic.class.getName();

    private static boolean startedPlaying = false;

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

    public void play(){
        Log.d(TAG, "Tried to play");
        playerControls.play();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) { Log.d(TAG, "STARTED PLAYING " + Boolean.toString(startedPlaying));}

            @Override
            public void onFinish() {
                Log.d(TAG, "Timer done");
                if(!startedPlaying) {
                    Log.d(TAG, "Tried to play/pause");
                    playerControls.pause();
                    playerControls.play();
                }
            }
        }.start();
    }

    @Override
    public void isPlaying() {
        startedPlaying = true;
    }

    @Override
    public void notPlaying() {
        startedPlaying = false;
    }
}

