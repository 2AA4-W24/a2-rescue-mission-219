package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BatteryLevelTest {

    @Test
    void testGetBatteryLevel() {
        BatteryLevel battery = new BatteryLevel(100);
        battery.getBatteryLevel();
        assertEquals(100, battery.getBatteryLevel());
    }

    @Test
    void testDecreaseBattery() {
        BatteryLevel battery = new BatteryLevel(100);
        battery.decreaseBattery(20);
        assertEquals(80, battery.getBatteryLevel());
    }

    @Test
    void testBatteryLevelLowFalse() {
        BatteryLevel battery = new BatteryLevel(50);
        assertFalse(battery.batteryLevelLow());
    }

    @Test
    void testBatteryLevelLowTrue() {
        BatteryLevel battery = new BatteryLevel(30);
        assertTrue(battery.batteryLevelLow());
    }

}

