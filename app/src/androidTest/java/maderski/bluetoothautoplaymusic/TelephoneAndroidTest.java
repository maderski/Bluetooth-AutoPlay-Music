package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created by Jason on 7/26/16.
 */
public class TelephoneAndroidTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testIsOnCall(){
        Context context = getContext();
        Telephone telephone = new Telephone(context);
        boolean isOnCall = telephone.isOnCall();
        assertEquals(false, isOnCall);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
