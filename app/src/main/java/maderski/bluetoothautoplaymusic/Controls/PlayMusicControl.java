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

    private PlayerControls playerControls;
    private FirebaseHelper mFirebaseHelper;

    private static CountDownTimer mCountDownTimer;
    private static Handler mHandler;
    private static Runnable mRunnable;

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
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
            Log.d(TAG, "Music Play Check CANCELLED");
            mCountDownTimer = null;
            return true;
        }

        if(mHandler != null && mRunnable != null){
            mHandler.removeCallbacks(mRunnable);
            Log.d(TAG, "Music Play Check CANCELLED");
            mHandler = null;
            mRunnable = null;
        }
        return false;
    }

    public synchronized void checkIfPlaying(final Context context, final int seconds){
            final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            // String[] nonKeyEventPlayers = { PackageTools.PackageName.GOOGLEPLAYMUSIC };
            final String selectedMusicPlayer = BAPMPreferences.getPkgSelectedMusicPlayer(context);
            // boolean shouldTryKeyEvent = !Arrays.asList(nonKeyEventPlayers).contains(selectedMusicPlayer);
            boolean isGooglePlayMusic = selectedMusicPlayer.equals(PackageTools.PackageName.GOOGLEPLAYMUSIC);

            if(isGooglePlayMusic){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        play();
                    }
                }, 2000);


                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "isMusicPlaying: " + Boolean.toString(audioManager.isMusicActive()));
                }

                mHandler = new Handler();
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "isMusicPlaying: " + Boolean.toString(audioManager.isMusicActive()));
                        }

                        if(!audioManager.isMusicActive()){
                            Log.d(TAG, "Play media Button");
                            playerControls.play_mediaButton();
                        }
                    }
                };
                mHandler.postDelayed(mRunnable, 8000);
            } else {
                final long milliseconds = seconds * 1000;
                mCountDownTimer = new CountDownTimer(milliseconds, 3000) {

                    @Override
                    public void onTick(long l) {
                        if (!audioManager.isMusicActive()) {
                            play();
                        }

                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "millsUntilFinished: " + Long.toString(l) +
                                    "isMusicPlaying: " + Boolean.toString(audioManager.isMusicActive()));
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (!audioManager.isMusicActive()) {
                            if (selectedMusicPlayer.equals(PackageTools.PackageName.PANDORA)) {
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
                            } else {
                                Log.d(TAG, "Final attempt to play");
                                playerControls.play_keyEvent();
                            }
                        } else {
                            Log.d(TAG, "onFinish Music is Active");
                        }
                        mFirebaseHelper.musicAutoPlay(audioManager.isMusicActive());
                        mCountDownTimer = null;
                    }
                }.start();
            }
    }
}

