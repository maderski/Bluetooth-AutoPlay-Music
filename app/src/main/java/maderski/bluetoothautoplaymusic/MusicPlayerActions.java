package maderski.bluetoothautoplaymusic;

import android.media.MediaPlayer;

/**
 * Created by Jason on 12/8/15.
 */
public class MusicPlayerActions {
    private String sTAG = this.getClass().getName();
    private static MediaPlayer mp = new MediaPlayer();

    //Checks to see if music is playing
    public static Boolean musicIsPlaying(){
        return mp.isPlaying();
    }
    //Need to test but should cause music player to start playing
    public static void startMusic(){
        mp.start();
    }

    public static void stopMusic(){
        mp.stop();
    }

    public static void pauseMusic(){
        mp.pause();
    }
}

