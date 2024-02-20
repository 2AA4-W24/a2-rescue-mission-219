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

    public boolean batteryLevelLow(int battery){
        if (battery == 1){ //asuming stoping doesnt require any battery
            return true;
        }
        return false;
    }
}