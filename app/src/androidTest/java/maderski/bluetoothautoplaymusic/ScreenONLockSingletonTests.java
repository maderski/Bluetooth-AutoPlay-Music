package maderski.bluetoothautoplaymusic;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

/**
 * Created by Jason on 8/3/16.
 */
public class ScreenONLockSingletonTests extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testWakeLockNotNull(){
        assertNotNull(ScreenONLock.getInstance());
    }

    @SmallTest
    public void testWakeLockIsEnabled(){
        ScreenONLock.getInstance().enableWakeLock(getContext());
        assertEquals(true, ScreenONLock.getInstance().wakeLockHeld());
    }

    @SmallTest
    public void testWakeLockIsDisabled(){
        ScreenONLock.getInstance().releaseWakeLock();
        assertEquals(false, ScreenONLock.getInstance().wakeLockHeld());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
