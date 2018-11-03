package maderski.bluetoothautoplaymusic.bus

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

/**
 * Created by Jason on 11/4/17.
 */

object BusProvider {
    @JvmStatic
    val busInstance = Bus(ThreadEnforcer.MAIN)
}
