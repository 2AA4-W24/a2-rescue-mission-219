package ca.mcmaster.se2aa4.island.team219;

public class BatteryLevel {

    private int battery;

    public BatteryLevel(int battery){
        this.battery = battery;
    }

    public int getBatteryLevel(){
        return battery;
    }

    public void decreaseBattery(int cost){
        battery = battery - cost;

    }

    public boolean batteryLevelLow(){
        if (this.battery == 30){ //asuming stoping requires this amount or less
            return true;
        }
        return false;
    }
}