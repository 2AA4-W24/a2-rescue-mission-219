package ca.mcmaster.se2aa4.island.team219;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BatteryTest {

    @Test
    void testGetBatteryLevel() {
        Battery battery = new Battery(100);
        int actualBatteryLevel = battery.getBatteryLevel();
        assertEquals(100, actualBatteryLevel, "Battery level should be initialized to 100.");
    }

    @Test
    void testDecreaseBattery() {
        Battery battery = new Battery(100);
        battery.decreaseBattery(20);
        assertEquals(80, battery.getBatteryLevel(), "Decreasing the battery by 20 should result in a battery level of 80.");
    }

}
