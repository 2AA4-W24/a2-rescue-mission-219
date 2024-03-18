package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BatteryLevelTest {

    @Test
    void testGetBatteryLevel() {
        Battery battery = new Battery(100);
        battery.getBatteryLevel();
        assertEquals(100, battery.getBatteryLevel());
    }

    @Test
    void testDecreaseBattery() {
        Battery battery = new Battery(100);
        battery.decreaseBattery(20);
        assertEquals(80, battery.getBatteryLevel());
    }

    @Test
    void testBatteryLevelLowFalse() {
        Battery battery = new Battery(50);
        assertFalse(battery.batteryLevelLow());
    }

    @Test
    void testBatteryLevelLowTrue() {
        Battery battery = new Battery(30);
        assertTrue(battery.batteryLevelLow());
    }

}

