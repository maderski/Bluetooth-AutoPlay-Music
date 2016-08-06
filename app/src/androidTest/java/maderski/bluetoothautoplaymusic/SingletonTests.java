package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created by Jason on 8/3/16.
 */
public class SingletonTests extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testWakeLockNotNull(){
        assertNotNull(Singleton.getInstance().getScreenONLock());
    }

    @SmallTest
    public void testWakeLockIsEnabled(){
        Singleton.getInstance().getScreenONLock().enableWakeLock(getContext());
        assertEquals(true, Singleton.getInstance().getScreenONLock().wakeLockHeld());
    }

    @SmallTest
    public void testWakeLockIsDisabled(){
        Singleton.getInstance().getScreenONLock().releaseWakeLock();
        assertEquals(false, Singleton.getInstance().getScreenONLock().wakeLockHeld());
    }

//    @SmallTest
//    public void testCurrentRingerSet(){
//        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
//        RingerControl ringerControl = new RingerControl(audioManager);
//        int tempRinger = ringerControl.ringerSetting();
//        Singleton.getInstance().setCurrentRingerSet(tempRinger);
//        assertEquals(tempRinger, Singleton.getInstance().getCurrentRingerSet());
//    }

//    @SmallTest
//    public void testIsSelected(){
//        Singleton.getInstance().setIsSelected(true);
//        Singleton.getInstance().setIsSelected(false);
//        Singleton.getInstance().setIsSelected(true);
//
//        assertEquals(true, Singleton.getInstance().getIsSelected());
//    }

//    @SmallTest
//    public void testRanActionsOnBTConnect(){
//        Singleton.getInstance().setRanActionsOnBTConnect(true);
//        Singleton.getInstance().setRanActionsOnBTConnect(false);
//        Singleton.getInstance().setRanActionsOnBTConnect(true);
//
//        assertEquals(true, Singleton.getInstance().getRanActionsOnBTConnect());
//    }

//    @SmallTest
//    public void testLaunchNotifPresent(){
//        Singleton.getInstance().setLaunchNotifPresent(true);
//        Singleton.getInstance().setLaunchNotifPresent(true);
//        Singleton.getInstance().setLaunchNotifPresent(true);
//
//        assertEquals(true, Singleton.getInstance().getLaunchNotifPresent());
//    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
