package maderski.bluetoothautoplaymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

/**
 * Created by Jason on 8/1/16.
 */
public abstract class PlayerControls {

    private AudioManager audioManager;

    public abstract void play();

    public PlayerControls(AudioManager audioManager){ this.audioManager = audioManager; }

    public void pause(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }
}

class BeyondPod extends PlayerControls{
    private Context context;

    public BeyondPod(Context context, AudioManager audioManager){
        super(audioManager);
        this.context = context;
    }
    @Override
    public void play() {
        Intent intent = new Intent();
        intent.setAction("mobi.beyondpod.command.PLAY");
        context.sendBroadcast(intent);
    }
}

class Spotify extends PlayerControls{
    private Context context;

    public Spotify(Context context, AudioManager audioManager){
        super(audioManager);
        this.context = context;
    }

    @Override
    public void play() {
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(i, null);

        i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(i, null);
    }
}

class GooglePlayMusic extends PlayerControls{
    private Context context;

    public GooglePlayMusic(Context context, AudioManager audioManager){
        super(audioManager);
        this.context = context;
    }

    @Override
    public void play() {
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "play");
        context.sendBroadcast(intent);
    }
}

class OtherMusicPlayer extends PlayerControls{
    private AudioManager audioManager;

    public OtherMusicPlayer(AudioManager audioManager){
        super(audioManager);
        this.audioManager = audioManager;
    }

    @Override
    public void play() {
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        audioManager.dispatchMediaKeyEvent(upEvent);

    }

}
