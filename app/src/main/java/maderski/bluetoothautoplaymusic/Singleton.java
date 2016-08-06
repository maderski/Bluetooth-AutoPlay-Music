package maderski.bluetoothautoplaymusic;

/**
 * Created by Jason on 8/1/16.
 */
public class Singleton {
    private static Singleton instance = null;

    private static ScreenONLock screenONLock = null;

    public static synchronized Singleton getInstance() {
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    private Singleton() {}

    public synchronized ScreenONLock getScreenONLock(){
        if(screenONLock == null)
            screenONLock = new ScreenONLock();
        return screenONLock;
    }
}
