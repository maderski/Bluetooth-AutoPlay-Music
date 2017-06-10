package maderski.bluetoothautoplaymusic.Controls;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusicControl {

    private static final String TAG = PlayMusicControl.class.getName();

    private static Handler mHandler;
    private static Runnable mRunnable;

    private PlayerControls playerControls;
    private FirebaseHelper mFirebaseHelper;

    public PlayMusicControl(Context context){
        setPlayerControls(context);
        mFirebaseHelper = new FirebaseHelper(context);
    }

    private void setPlayerControls(final Context context){
        String pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context);
        Log.d(TAG, "PLAYER: " + pkgName);
        switch (pkgName) {
            case PackageTools.PackageName.SPOTIFY:
                playerControls = new Spotify(context);
                break;
            case PackageTools.PackageName.BEYONDPOD:
                playerControls = new BeyondPod(context);
                break;
            case PackageTools.PackageName.FMINDIA:
                playerControls = new FMIndia(context);
                break;
            case PackageTools.PackageName.GOOGLEPLAYMUSIC:
                playerControls = new GooglePlayMusic(context);
                break;
            case PackageTools.PackageName.PANDORA:
                playerControls = new Pandora(context);
                break;
            default:
                playerControls = new OtherMusicPlayer(context);
                break;
        }
    }

    public void pause(){ playerControls.pause(); }

    public void play(){
        Log.d(TAG, "Tried to play");
        playerControls.play();
    }

    public static boolean cancelCheckIfPlaying(){
        if(mHandler != null && mRunnable != null){
            mHandler.removeCallbacks(mRunnable);
            Log.d(TAG, "Music Play Check CANCELLED");
            mHandler = null;
            mRunnable = null;
        }
        return false;
    }

    public synchronized void checkIfPlaying(final Context context, final int seconds){
        final int milliSeconds = seconds * 1000;
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        final String selectedMusicPlayer = BAPMPreferences.getPkgSelectedMusicPlayer(context);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }, 3000);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "isMusicPlaying: " + Boolean.toString(audioManager.isMusicActive()));
                }

                if(!audioManager.isMusicActive()){
                    switch (selectedMusicPlayer){
                        case PackageTools.PackageName.PANDORA:
                            finalAttemptToPlayPandora(context, audioManager);
                            break;
                        default:
                            Log.d(TAG, "Play media Button");
                            playerControls.play_mediaButton(selectedMusicPlayer);
                            break;
                    }
                }

                mFirebaseHelper.musicAutoPlay(audioManager.isMusicActive());
            }
        };
        mHandler.postDelayed(mRunnable, milliSeconds);
    }

    private void finalAttemptToPlayPandora(final Context context, final AudioManager audioManager){
        final LaunchApp launchApp = new LaunchApp();
        launchApp.launchPackage(context, PackageTools.PackageName.PANDORA);
        Log.d(TAG, "PANDORA LAUNCHED");

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (BAPMPreferences.getLaunchGoogleMaps(context)) {
                    String choosenMapApp = BAPMPreferences.getMapsChoice(context);
                    launchApp.launchPackage(context, choosenMapApp);
                }
            }
        };
        handler.postDelayed(runnable, 4000);
    }
}

