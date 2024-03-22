package ca.mcmaster.se2aa4.island.team219;

public class Battery {

    private int batteryLevel;

    public Battery(int battery) {
        this.batteryLevel = battery;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void decreaseBattery(int cost) {
        batteryLevel = batteryLevel - cost;
    }

    public boolean batteryLevelLow() {
        return this.batteryLevel <= 40; //asuming stoping requires this amount or less
    }
}