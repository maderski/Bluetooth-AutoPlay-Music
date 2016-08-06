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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
