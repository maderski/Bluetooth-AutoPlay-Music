package maderski.bluetoothautoplaymusic.controls;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControls;
import maderski.bluetoothautoplaymusic.controls.playercontrols.PlayerControlsFactory;
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusicControl {

    private static final String TAG = "PlayMusicControl";

    private static Handler mHandler;
    private static Runnable mRunnable;

    private PlayerControls mPlayerControls;
    private FirebaseHelper mFirebaseHelper;

    public PlayMusicControl(Context context){
        String pkgName = BAPMPreferences.getPkgSelectedMusicPlayer(context);
        Log.d(TAG, "PLAYER: " + pkgName);

        mPlayerControls = PlayerControlsFactory.getPlayerControl(context, pkgName);
        mFirebaseHelper = new FirebaseHelper(context);
    }

    public void pause(){ mPlayerControls.pause(); }

    public void play(){
        Log.d(TAG, "Tried to play");
        mPlayerControls.play();
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
                if(!audioManager.isMusicActive()) {
                    play();
                }
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
                            mPlayerControls.playMediaButton(selectedMusicPlayer);
                            break;
                    }
                }

                mFirebaseHelper.musicAutoPlay(audioManager.isMusicActive());
            }
        };
        mHandler.postDelayed(mRunnable, milliSeconds);
    }

    private void finalAttemptToPlayPandora(final Context context, final AudioManager audioManager){
        final LaunchAppHelper launchAppHelper = new LaunchAppHelper();
        launchAppHelper.launchPackage(context, PackageTools.PackageName.PANDORA);
        Log.d(TAG, "PANDORA LAUNCHED");

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (BAPMPreferences.getLaunchGoogleMaps(context)) {
                    String choosenMapApp = BAPMPreferences.getMapsChoice(context);
                    launchAppHelper.launchPackage(context, choosenMapApp);
                }
            }
        };
        handler.postDelayed(runnable, 4000);
    }
}

