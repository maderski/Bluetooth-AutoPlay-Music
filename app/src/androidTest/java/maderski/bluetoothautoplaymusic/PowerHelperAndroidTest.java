package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import maderski.bluetoothautoplaymusic.Helpers.PowerHelper;

/**
 * Created by Jason on 7/26/16.
 */
public class PowerHelperAndroidTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testIsPluggedIn(){
        Context context = getContext();
        boolean isPluggedIn = PowerHelper.isPluggedIn(context);
        assertEquals(true, isPluggedIn);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
