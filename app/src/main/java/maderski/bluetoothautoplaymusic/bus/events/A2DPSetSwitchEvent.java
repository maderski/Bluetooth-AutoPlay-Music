package maderski.bluetoothautoplaymusic.bus.events;

/**
 * Created by Jason on 11/4/17.
 */

public class A2DPSetSwitchEvent {
    private boolean useA2DP;

    public A2DPSetSwitchEvent(boolean useA2DP) {
        this.useA2DP = useA2DP;
    }

    public boolean isUsingA2DP() {
        return useA2DP;
    }
}
