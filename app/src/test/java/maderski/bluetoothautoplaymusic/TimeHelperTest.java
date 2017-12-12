package maderski.bluetoothautoplaymusic;

import org.junit.Test;

import maderski.bluetoothautoplaymusic.Helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.Helpers.TimeHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jason on 11/18/17.
 */

public class TimeHelperTest {
    @Test
    public void testCustomStartAtNightAndEndAtMorning() {
        int startTime = 2300;
        int endTime = 800;

        System.out.println("CUSTOM START AT NIGHT END AT MORNING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(startTime, endTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= endTime || currentTime >= startTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }
    }

    @Test
    public void testCustomStartAtMorningEndAtMorning() {
        int startTime = 800;
        int endTime = 1000;

        System.out.println("CUSTOM START AT MORNING END AT MORNING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(startTime, endTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= endTime && currentTime >= startTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }
    }

    @Test
    public void testCustomStartAtMorningEndAtEvening() {
        int startTime = 1100;
        int endTime = 1300;

        System.out.println("CUSTOM START AT MORNING END AT EVENING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(startTime, endTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= endTime && currentTime >= startTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }
    }

    @Test
    public void testCustomStartAtEveningEndAtEvening() {
        int startTime = 1600;
        int endTime = 1700;

        System.out.println("CUSTOM START AT EVENING END AT EVENING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(startTime, endTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= endTime && currentTime >= startTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }
    }

    @Test
    public void testGetting12hrFormatedTime() {
        int time24hr1 = 800;
        int time24hr2 = 1200;
        int time24hr3 = 1600;
        int time24hr4 = 1630;
        int time24hr5 = 2215;

        String time12hr1 = "8:00 AM";
        String time12hr2 = "12:00 PM";
        String time12hr3 = "4:00 PM";
        String time12hr4 = "4:30 PM";
        String time12hr5 = "10:15 PM";

        String convertedTo12hrTime = TimeHelper.get12hrTime(time24hr1);
        assertEquals(convertedTo12hrTime, time12hr1);

        convertedTo12hrTime = TimeHelper.get12hrTime(time24hr2);
        assertEquals(convertedTo12hrTime, time12hr2);

        convertedTo12hrTime = TimeHelper.get12hrTime(time24hr3);
        assertEquals(convertedTo12hrTime, time12hr3);

        convertedTo12hrTime = TimeHelper.get12hrTime(time24hr4);
        assertEquals(convertedTo12hrTime, time12hr4);

        convertedTo12hrTime = TimeHelper.get12hrTime(time24hr5);
        assertEquals(convertedTo12hrTime, time12hr5);

    }

    @Test
    public void testGettingCurrentTimeIn24hrTime() {
        int currentTime = TimeHelper.getCurrent24hrTime();
        System.out.println(currentTime);
        // TODO:// Figure out a way to check this
    }

    @Test
    public void testMorningWithinTimeSpan() {
        int morningStartTime = 2000;
        int morningEndTime = 700;

        System.out.println("Morning START AT NIGHT END AT MORNING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(morningStartTime, morningEndTime, 0, 0, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= morningEndTime || currentTime >= morningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }

        morningStartTime = 800;
        morningEndTime = 1000;

        System.out.println("Morning START AT MORNING END AT MORNING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(morningStartTime, morningEndTime, 2500, 2500, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= morningEndTime && currentTime >= morningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }

        morningStartTime = 1000;
        morningEndTime = 1300;

        System.out.println("Morning START AT MORNING END AT EVENING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(morningStartTime, morningEndTime, 2500, 2500, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= morningEndTime && currentTime >= morningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }

        morningStartTime = 1600;
        morningEndTime = 1800;

        System.out.println("Morning START AT EVENING END AT NIGHT");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(morningStartTime, morningEndTime, 2500, 2500, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= morningEndTime && currentTime >= morningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }
    }

    @Test
    public void testEveningTimeSpan() {
        int eveningStartTime = 2000;
        int eveningEndTime = 700;

        System.out.println("Evening START AT NIGHT END AT MORNING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(0, 0, eveningStartTime, eveningEndTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= eveningEndTime || currentTime >= eveningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }

        eveningStartTime = 800;
        eveningEndTime = 1000;

        System.out.println("Evening START AT MORNING END AT MORNING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(2500, 2500, eveningStartTime, eveningEndTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= eveningEndTime && currentTime >= eveningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }

        eveningStartTime = 1000;
        eveningEndTime = 1300;

        System.out.println("Evening START AT MORNING END AT EVENING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(2500, 2500, eveningStartTime, eveningEndTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= eveningEndTime && currentTime >= eveningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }

        eveningStartTime = 1600;
        eveningEndTime = 1800;

        System.out.println("Evening START AT MORNING END AT EVENING");
        for(int currentTime = 0; currentTime < 2500; currentTime += 100) {
            TimeHelper timeHelper = new TimeHelper(2500, 2500, eveningStartTime, eveningEndTime, currentTime);
            boolean isWithInTimeSpan = timeHelper.isWithinTimeSpan();
            System.out.println(String.valueOf(currentTime) + " " + String.valueOf(isWithInTimeSpan));

            if(currentTime <= eveningEndTime && currentTime >= eveningStartTime) {
                assertTrue(isWithInTimeSpan);
            } else {
                assertFalse(isWithInTimeSpan);
            }
        }
    }

    @Test
    public void testGetDirectionsLocationCustom() {
        int currentTime = 1400;
        int startTime = 1300;
        int endTime = 1500;

        TimeHelper timeHelper = new TimeHelper(startTime, endTime, currentTime);
        String directionLocation = timeHelper.getDirectionLocation();

        assertEquals(directionLocation, LaunchAppHelper.DirectionLocations.CUSTOM);
    }

    @Test
    public void testGetDirectionsLocationWork() {
        int currentTime = 930;
        int morningStart = 800;
        int morningEnd = 1000;
        int eveningStart = 1600;
        int eveningEnd = 1800;

        TimeHelper timeHelper = new TimeHelper(morningStart, morningEnd, eveningStart, eveningEnd, currentTime);
        String directionLocation = timeHelper.getDirectionLocation();

        assertEquals(directionLocation, LaunchAppHelper.DirectionLocations.WORK);
    }

    @Test
    public void testGetDirectionsLocationHome() {
        int currentTime = 1630;
        int morningStart = 800;
        int morningEnd = 1000;
        int eveningStart = 1600;
        int eveningEnd = 1800;

        TimeHelper timeHelper = new TimeHelper(morningStart, morningEnd, eveningStart, eveningEnd, currentTime);
        String directionLocation = timeHelper.getDirectionLocation();

        assertEquals(directionLocation, LaunchAppHelper.DirectionLocations.HOME);
    }

}
