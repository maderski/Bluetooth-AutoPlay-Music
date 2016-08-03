package maderski.bluetoothautoplaymusic;

/**
 * Created by Jason on 8/1/16.
 */
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();

    private static ScreenONLock screenONLock = null;
    private static boolean ranActionsOnBTConnect;

    private static boolean isSelected;
    private static boolean launchNotifPresent;

    private static int currentRingerSet;

    public static Singleton getInstance() { return INSTANCE; }

    private Singleton() {}

    public ScreenONLock getScreenONLock(){
        if(screenONLock == null)
            screenONLock = new ScreenONLock();
        return screenONLock;
    }

    public void setIsSelected(boolean isSelectedDevice){ isSelected = isSelectedDevice; }
    public boolean getIsSelected(){ return isSelected; }

    public void setRanActionsOnBTConnect(boolean didItRun){ ranActionsOnBTConnect = didItRun; }
    public boolean getRanActionsOnBTConnect(){ return ranActionsOnBTConnect; }

    public void setCurrentRingerSet(int currentRinger){ currentRingerSet = currentRinger; }
    public int getCurrentRingerSet(){ return currentRingerSet; }

    public void setLaunchNotifPresent(boolean notifPresent){ launchNotifPresent = notifPresent; }
    public boolean getLaunchNotifPresent(){ return launchNotifPresent; }








}
