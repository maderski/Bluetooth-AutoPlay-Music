package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created by Jason on 7/26/16.
 */
public class PowerAndroidTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testIsPluggedIn(){
        Context context = getContext();
        boolean isPluggedIn = Power.isPluggedIn(context);
        assertEquals(true, isPluggedIn);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
