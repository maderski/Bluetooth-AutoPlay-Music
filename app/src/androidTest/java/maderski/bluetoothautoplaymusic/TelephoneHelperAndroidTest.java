package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import maderski.bluetoothautoplaymusic.helpers.TelephoneHelper;

/**
 * Created by Jason on 7/26/16.
 */
public class TelephoneHelperAndroidTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testIsOnCall(){
        Context context = getContext();
        TelephoneHelper telephoneHelper = new TelephoneHelper(context);
        boolean isOnCall = telephoneHelper.isOnCall();
        assertEquals(false, isOnCall);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
