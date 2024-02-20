package ca;

public class batteryLevel {

    private int battery;

    public batteryLevel(int battery){
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
