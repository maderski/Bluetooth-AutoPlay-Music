package maderski.bluetoothautoplaymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic {

    private static String TAG = PlayMusic.class.getName();

    private AudioManager _audioManager;
    private Context _context;

    public PlayMusic(Context context, AudioManager audioManager){
        _audioManager = audioManager;
        _context = context;
    }

    //Not used
    public void playButton(){

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        _context.sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        _context.sendOrderedBroadcast(upIntent, null);
    }

    //KeyEvent PLAY
    public void play(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        _audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        _audioManager.dispatchMediaKeyEvent(upEvent);
    }

    //KeyEvent PAUSE
    public void pause(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
        _audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE);
        _audioManager.dispatchMediaKeyEvent(upEvent);
    }

    public void play_UsingServiceCommand(){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "play");
        _context.sendBroadcast(intent);
    }

    //Play Google Play Music
    public void play_googlePlayMusic(){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.setPackage(ConstantStore.GOOGLEPLAYMUSIC);
        intent.putExtra("command", "play");
        _context.sendBroadcast(intent);
    }

    public void pause_googlePlayMusic(){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.setPackage(ConstantStore.GOOGLEPLAYMUSIC);
        intent.putExtra("command", "pause");
        _context.sendBroadcast(intent);
    }

    //Play Spotify
    public void play_spotify(){
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        _context.sendOrderedBroadcast(i, null);

        i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
        _context.sendOrderedBroadcast(i, null);
    }

    //Autoplay music if enabled
    public void auto_Play(){
        int index = BAPMPreferences.getSelectedMusicPlayer(_context);
        String pkgName = VariousLists.listOfInstalledMediaPlayers(_context).get(index);

        switch (pkgName) {
            case ConstantStore.SPOTIFY:
                play_spotify();
                break;
            case ConstantStore.GOOGLEPLAYMUSIC:
                play_UsingServiceCommand();
                break;
            default:
                play();
                break;
        }

        if(BuildConfig.DEBUG)
            Log.i(TAG, "Is Music Active: " + Boolean.toString(_audioManager.isMusicActive()));

        checkIfPlaying(_audioManager, pkgName);
    }

    private void checkIfPlaying(final AudioManager am, final String packageName){
        //Wait 3 seconds then, check if music is playing every second for 10 seconds
        new CountDownTimer(13000, 1000){
            boolean useServiceCommand = false;
            @Override
            public void onTick(long millisUntilFinished) {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "millisUntilFinished: " + Long.toString(millisUntilFinished));
                if(millisUntilFinished <= 10000) {
                    if (am.isMusicActive()) {
                        if (BuildConfig.DEBUG)
                            Log.i(TAG, "Music is playing");
                        cancel();
                    } else {
                        if(packageName.equalsIgnoreCase(ConstantStore.SPOTIFY)){
                            play_spotify();
                        }else{
                            useServiceCommand = !useServiceCommand;
                            playToggle(useServiceCommand);
                        }
                    }

                    if (!am.isBluetoothA2dpOn()) {
                        if (BuildConfig.DEBUG)
                            Log.i(TAG, "Bluetooth is not connected");
                        pause();
                        cancel();
                    }
                }
            }

            @Override
            public void onFinish() {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Unable to Play :(");
            }
        }.start();
    }

    private void playToggle(boolean _useServiceCommand) {
        if(BuildConfig.DEBUG)
            Log.i(TAG, "Use Service Command: " + Boolean.toString(_useServiceCommand));
        //if playAttempt number is even play using service command
        if (_useServiceCommand) {
            play_UsingServiceCommand();
            if (BuildConfig.DEBUG)
                Log.i(TAG, "Pressed Play again using service command");
        } else {
            pause();
            play();
            if (BuildConfig.DEBUG)
                Log.i(TAG, "Pressed Play again");
        }
    }
}

