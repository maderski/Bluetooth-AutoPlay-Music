package maderski.bluetoothautoplaymusic;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;

import maderski.bluetoothautoplaymusic.ui.activities.MainActivity;

/**
 * Created by Jason on 7/26/16.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    @SmallTest
    public void testOnConnectButtons(){
        Button mapButton = (Button)getActivity().findViewById(R.id.MapsToggleButton);
        assertNotNull(mapButton);

        Button keepONButton = (Button)getActivity().findViewById(R.id.KeepONToggleButton);
        assertNotNull(keepONButton);

        Button priorityButton = (Button)getActivity().findViewById(R.id.PriorityToggleButton);
        assertNotNull(priorityButton);

        Button unlockButton = (Button)getActivity().findViewById(R.id.UnlockToggleButton);
        assertNotNull(unlockButton);

        Button volumeMAXButton = (Button)getActivity().findViewById(R.id.VolumeMAXToggleButton);
        assertNotNull(volumeMAXButton);

        Button launchMusicPlayerButton = (Button)getActivity().findViewById(R.id.LaunchMusicPlayerToggleButton);
        assertNotNull(launchMusicPlayerButton);
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
