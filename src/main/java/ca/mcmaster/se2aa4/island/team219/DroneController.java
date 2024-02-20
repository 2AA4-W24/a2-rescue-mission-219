package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class DroneController implements Drone {

    private Information currentInformation; 
    private BatteryLevel batteryLevel; 
    private Turn initialDirection;

    public DroneController(int battery, Turn direction){
        this.initialDirection = direction;
        this.batteryLevel = new BatteryLevel(battery);
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }

    @Override
    public JSONObject makeDecision() {
        JSONObject decision = new JSONObject();

        if (this.batteryLevel.batteryLevelLow()) { 
            decision.put("action", "stop");
        } else {
        }
        return decision;
    }

    @Override
    public JSONObject turnLeft(){
        JSONObject decision = new JSONObject();
        initialDirection = initialDirection.left();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", initialDirection.toString().toUpperCase()));
        return decision;
    }

    @Override
    public JSONObject turnRight(){
        JSONObject decision = new JSONObject();
        initialDirection = initialDirection.right();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", initialDirection.toString().toUpperCase()));
        return decision;
    }
}
