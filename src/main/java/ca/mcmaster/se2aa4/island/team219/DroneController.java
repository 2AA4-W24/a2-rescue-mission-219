package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class DroneController {

    private Information currentInformation; 
    private BatteryLevel batteryLevel; 
    private Turn initialDirection;

    public DroneController(int battery, Turn direction){
        this.initialDirection = direction;
        this.batteryLevel = new BatteryLevel(battery);
    }

    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }

    public JSONObject makeDecision() {
        JSONObject decision = new JSONObject();

        if (this.batteryLevel.batteryLevelLow()) { 
            decision.put("action", "stop");
        } else {
        }
        return decision;
    }
}
