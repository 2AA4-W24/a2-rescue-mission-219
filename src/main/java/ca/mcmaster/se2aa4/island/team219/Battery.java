package ca.mcmaster.se2aa4.island.team219;

public class Battery {

    private int battery;

    public Battery(int battery) {
        this.battery = battery;
    }

    public int getBatteryLevel() {
        return battery;
    }

    public void decreaseBattery(int cost) {
        battery = battery - cost;
    }

    public boolean batteryLevelLow() {
        if (this.battery <= 40) { //asuming stoping requires this amount or less
            return true;
        }
        return false;
    }
}