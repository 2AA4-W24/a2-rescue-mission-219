package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class DroneController {

    private Information currentInformation; 
    private BatteryLevel batteryLevel; 

    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }

    public JSONObject makeDecision() {
        JSONObject decision = new JSONObject();

        if (this.batteryLevel.getBatteryLevel() <= 1) { 
            decision.put("action", "stop");
        } else {
            
        }
        return decision;
    }
}
