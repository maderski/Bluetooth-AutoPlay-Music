package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Created by Jason on 12/8/15.
 */
public class PlayMusic {


    private static String sTAG = PlayMusic.class.getName();

    //Need to test but should cause music player to start playing
    public static void start(Context context){

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        context.sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        context.sendOrderedBroadcast(upIntent, null);
        
    }
}

