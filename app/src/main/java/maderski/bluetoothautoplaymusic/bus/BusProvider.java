package maderski.bluetoothautoplaymusic.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Jason on 11/4/17.
 */

public class BusProvider {
    private static final Bus BUS = new Bus(ThreadEnforcer.MAIN);

    public static Bus getBusInstance() {
        return BUS;
    }

    private BusProvider() {}
}
