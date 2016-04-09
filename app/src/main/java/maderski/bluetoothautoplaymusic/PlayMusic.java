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

    //Not used
    public static void playButton(Context context){

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
    public static void play(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        VariableStore.am.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        VariableStore.am.dispatchMediaKeyEvent(upEvent);
    }

    //KeyEvent PAUSE
    public static void pause(){
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
        VariableStore.am.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE);
        VariableStore.am.dispatchMediaKeyEvent(upEvent);
    }

    public static void play_UsingServiceCommand(Context context){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "play");
        context.sendBroadcast(intent);
    }

    //Play Google Play Music
    public static void play_googlePlayMusic(Context context){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.setPackage(ConstantStore.GOOGLEPLAYMUSIC);
        intent.putExtra("command", "play");
        context.sendBroadcast(intent);
    }

    public static void pause_googlePlayMusic(Context context){
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.setPackage(ConstantStore.GOOGLEPLAYMUSIC);
        intent.putExtra("command", "pause");
        context.sendBroadcast(intent);
    }

    //Play Spotify
    public static void play_spotify(Context context){
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
    public static void auto_Play(final Context context){
        int index = BAPMPreferences.getSelectedMusicPlayer(context);
        String pkgName = VariousLists.listOfInstalledMediaPlayers(context).get(index);

        switch (pkgName) {
            case ConstantStore.SPOTIFY:
                play_spotify(context);
                break;
            case ConstantStore.GOOGLEPLAYMUSIC:
                play_UsingServiceCommand(context);
                //play_googlePlayMusic(context);
                break;
            default:
                play();
        }
        Log.i(TAG, "Is Music Active: " + Boolean.toString(VariableStore.am.isMusicActive()));
    }

    public static void checkIfPlaying(final Context context){
        new CountDownTimer(3000, 1000)
        {
            public void onTick(long millisUntilFinished) {
                if(VariableStore.am.isMusicActive()){
                    Log.i(TAG, "3 sec Is Music Active: " + Boolean.toString(VariableStore.am.isMusicActive()));
                    cancel();
                }else{
                    Log.i(TAG, "3 sec On Tick: Is Music Active: " + Boolean.toString(VariableStore.am.isMusicActive()));
                }
            }

            public void onFinish() {
                //For 10 seconds, each second check to see if music is playing, if not try a keyEvent play
                if(!VariableStore.am.isMusicActive()) {
                    new CountDownTimer(10000, 1000)
                    {
                        Boolean useServiceCommand = false;

                        public void onTick(long millisUntilFinished) {
                            Log.i(TAG, "On Tick: Is Music Active: " + Boolean.toString(VariableStore.am.isMusicActive()));
                            if (!VariableStore.am.isMusicActive()) {
                                if(!useServiceCommand){
                                    pause();
                                    play();
                                    Log.i(TAG, "Pressed Play again");
                                    useServiceCommand = true;
                                }else if(useServiceCommand){
                                    play_UsingServiceCommand(context);
                                    Log.i(TAG, "Pressed Play again using service command");
                                    useServiceCommand = false;
                                }
                            }else if(VariableStore.am.isMusicActive()){
                                cancel();
                            }else if(!VariableStore.isBTConnected){
                                Log.i(TAG, "Bluetooth is not connected");
                                pause();
                                cancel();
                            }
                        }

                        public void onFinish() {
                            Log.i(TAG, "Unable to Play :(");
                        }
                    }.start();
                }
            }
        }.start();
    }
}

