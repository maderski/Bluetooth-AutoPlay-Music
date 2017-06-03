package maderski.bluetoothautoplaymusic.Controls;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

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

    public PlayMusicControl(Context context){
        setPlayerControls(context);
        mFirebaseHelper = new FirebaseHelper(context);
    }

    private void setPlayerControls(Context context){
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
        return false;
    }

    public synchronized void checkIfPlaying(final Context context, final int seconds){
            long milliseconds = seconds * 1000;
            mCountDownTimer = new CountDownTimer(milliseconds, 1000) {
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

            @Override
            public void onTick(long l) {
                if(!audioManager.isMusicActive()){
                    play();
                }

                if(BuildConfig.DEBUG){
                    Log.d(TAG, "millsUntilFinished: " + Long.toString(l) +
                            "isMusicPlaying: " + Boolean.toString(audioManager.isMusicActive()));
                }
            }

            @Override
            public void onFinish() {
                if(!audioManager.isMusicActive()){
                    String selectedMusicPlayer = BAPMPreferences.getPkgSelectedMusicPlayer(context);

                    if(selectedMusicPlayer.equals(PackageTools.PackageName.PANDORA)){
                        final LaunchApp launchApp = new LaunchApp();
                        launchApp.launchPackage(context, PackageTools.PackageName.PANDORA);
                        Log.d(TAG, "PANDORA LAUNCHED");

                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if(BAPMPreferences.getLaunchGoogleMaps(context)){
                                    String choosenMapApp = BAPMPreferences.getMapsChoice(context);
                                    launchApp.launchPackage(context, choosenMapApp);
                                }
                            }
                        };
                        handler.postDelayed(runnable, 3000);
                    } else if(!selectedMusicPlayer.equals(PackageTools.PackageName.GOOGLEPLAYMUSIC)) {
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

