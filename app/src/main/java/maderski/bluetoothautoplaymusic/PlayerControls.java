package maderski.bluetoothautoplaymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 8/1/16.
 */
public abstract class PlayerControls {
    public AudioManager mAudioManager;
    public Context mContext;

    public abstract void play();

    public PlayerControls(Context context){
        this.mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        this.mContext = context;
    }

    public void pause(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
        mAudioManager.dispatchMediaKeyEvent(downEvent);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE);
                mAudioManager.dispatchMediaKeyEvent(upEvent);
            }
        }, 125);
    }

    public void play_pause(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        mAudioManager.dispatchMediaKeyEvent(downEvent);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                mAudioManager.dispatchMediaKeyEvent(upEvent);
            }
        }, 125);
    }
}

class BeyondPod extends PlayerControls{
    private static final String TAG = "BeyondPod";

    public BeyondPod(Context context){
        super(context);
    }
    @Override
    public void play() {
        Log.d(TAG, "Beyond Pod Music");
        Intent intent = new Intent();
        intent.setAction("mobi.beyondpod.command.PLAY");
        mContext.sendBroadcast(intent);
    }
}

class Spotify extends PlayerControls{
    private static final String TAG = "Spotify";

    public Spotify(Context context){
        super(context);
    }

    @Override
    public void play() {
        Log.d(TAG, "Spotify Play Music");
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.setComponent(new ComponentName(PackageTools.PackageName.SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
        i.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        mContext.sendOrderedBroadcast(i, null);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
                i.setComponent(new ComponentName(PackageTools.PackageName.SPOTIFY, "com.spotify.music.internal.receiver.MediaButtonReceiver"));
                i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
                mContext.sendOrderedBroadcast(i, null);
            }
        }, 125);
    }
}

class GooglePlayMusic extends PlayerControls{
    private static final String TAG = "GooglePlayMusic";

    public GooglePlayMusic(Context context){
        super(context);
    }

    @Override
    public void play() {
        Log.d(TAG, "Play Google Play Music");
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "play");
        mContext.sendBroadcast(intent);
    }
}

class AppleMusic extends PlayerControls {
    private static final String TAG = "AppleMusic";

    public AppleMusic(Context context){
        super(context);
    }

    @Override
    public void play() {

    }
}

class OtherMusicPlayer extends PlayerControls{
    private static final String TAG = "OtherMusicPlayer";

    public OtherMusicPlayer(Context context){
        super(context);
    }

    @Override
    public void play() {
        Log.d(TAG, "Other Play Music");
        String packageName = BAPMPreferences.getPkgSelectedMusicPlayer(mContext);

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        downIntent.setPackage(packageName);
        mContext.sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        upIntent.setPackage(packageName);
        mContext.sendOrderedBroadcast(upIntent, null);
    }

}
