package maderski.bluetoothautoplaymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic {

    private static final String TAG = PlayMusic.class.getName();

    private AudioManager audioManager;
    private Context context;

    public PlayMusic(Context context, AudioManager audioManager){
        this.audioManager = audioManager;
        this.context = context;
    }

    //Not used
    public void playButton(){

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        context.sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        context.sendOrderedBroadcast(upIntent, null);
    }

    //KeyEvent PLAY
    public void play(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }

    //KeyEvent PAUSE
    public void pause(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }

    public void play_UsingServiceCommand(){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "play");
        context.sendBroadcast(intent);
    }

    //Play Google Play Music
    public void play_googlePlayMusic(){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.setPackage(PackageTools.GOOGLEPLAYMUSIC);
        intent.putExtra("command", "play");
        context.sendBroadcast(intent);
    }

    public void pause_googlePlayMusic(){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.setPackage(PackageTools.GOOGLEPLAYMUSIC);
        intent.putExtra("command", "pause");
        context.sendBroadcast(intent);
    }

    //Play Spotify
    public void play_spotify(){
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(i, null);

        i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(i, null);
    }

    //Autoplay music if enabled
    public void auto_Play(){
        int index = BAPMPreferences.getSelectedMusicPlayer(context);
        String pkgName = VariousLists.listOfInstalledMediaPlayers(context).get(index);

        switch (pkgName) {
            case PackageTools.SPOTIFY:
                play_spotify();
                break;
            case PackageTools.GOOGLEPLAYMUSIC:
                play_UsingServiceCommand();
                break;
            default:
                play();
                break;
        }

        if(BuildConfig.DEBUG)
            Log.i(TAG, "Is Music Active: " + Boolean.toString(audioManager.isMusicActive()));
    }

    public void checkIfPlaying(){
        final AudioManager am = audioManager;
        new CountDownTimer(13000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "millisUntilFinished: " + Long.toString(millisUntilFinished));

                if (am.isMusicActive()) {
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "Music is playing");
                    if(millisUntilFinished < 8000) {
                        if (BuildConfig.DEBUG)
                            Log.i(TAG, "checkIfPlaying cancelled");
                        cancel();
                    }
                } else {
                    auto_Play();
                }

                if(!am.isBluetoothA2dpOn()){
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "Bluetooth is not connected");
                    pause();
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Unable to Play :(");
            }
        }.start();
    }
}

