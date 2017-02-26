package maderski.bluetoothautoplaymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import maderski.bluetoothautoplaymusic.Services.BAPMService;

/**
 * Created by Jason on 8/1/16.
 */
public abstract class PlayerControls {
    private AudioManager audioManager;

    public abstract void play();

    public PlayerControls(Context context){
        this.audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void pause(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
        audioManager.dispatchMediaKeyEvent(downEvent);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE);
                audioManager.dispatchMediaKeyEvent(upEvent);
            }
        }, 125);
    }
}

class BeyondPod extends PlayerControls{
    private static final String TAG = "BeyondPod";
    private Context context;

    public BeyondPod(Context context){
        super(context);
        this.context = context;
    }
    @Override
    public void play() {
        Log.d(TAG, "Beyond Pod Music");
        Intent intent = new Intent();
        intent.setAction("mobi.beyondpod.command.PLAY");
        context.sendBroadcast(intent);
    }
}

class Spotify extends PlayerControls{
    private static final String TAG = "Spotify";
    private Context context;

    public Spotify(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public void play() {
        Log.d(TAG, "Spotify Play Music");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName(PackageTools.SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(i, null);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                i.setComponent(new ComponentName(PackageTools.SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
                context.sendOrderedBroadcast(i, null);
            }
        }, 125);
    }
}

class GooglePlayMusic extends PlayerControls{
    private static final String TAG = "GooglePlayMusic";
    private Context context;

    public GooglePlayMusic(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public void play() {
        Log.d(TAG, "Play Google Play Music");
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "play");
        context.sendBroadcast(intent);
    }
}

class OtherMusicPlayer extends PlayerControls{
    private static final String TAG = "OtherMusicPlayer";
    private AudioManager audioManager;

    public OtherMusicPlayer(Context context){
        super(context);
        this.audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void play() {
        Log.d(TAG, "Other Play Music");

        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        audioManager.dispatchMediaKeyEvent(downEvent);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
                audioManager.dispatchMediaKeyEvent(upEvent);
            }
        }, 125);
    }

}
