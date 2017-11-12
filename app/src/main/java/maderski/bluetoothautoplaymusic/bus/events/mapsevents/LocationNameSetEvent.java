package maderski.bluetoothautoplaymusic.bus.events.mapsevents;

/**
 * Created by Jason on 11/11/17.
 */

public class LocationNameSetEvent {

    private String locationName;

    public LocationNameSetEvent(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }
}
