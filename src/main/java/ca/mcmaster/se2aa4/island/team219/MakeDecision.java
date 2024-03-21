package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class MakeDecision {

    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Battery batteryLevel; 
    private Compass currentDirection;
    private FindLand droneToLand;
    private GridSearch droneGridSearch;
    private boolean firstRun;
    private boolean secondRun;
    

    public MakeDecision(Integer battery, Compass direction) {
        this.currentDirection = direction;
        int batteryInt = battery.intValue(); 
        this.batteryLevel = new Battery(batteryInt);
        this.firstRun = true;
        this.secondRun = true;
        this.droneToLand = new FindLand(currentDirection);
        data = new AcknowledgeResults();
    }
    
    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }

    public int getBatteryLevelDrone() {
        return this.batteryLevel.getBatteryLevel();
    }
    
    public boolean batteryLevelWarning(){
        if (getBatteryLevelDrone() <= 40){
            return true;
        }else{
            return false;
        }
    }

    public String getClosestCreek(){
        return droneGridSearch.getClosestCreek();
    }

    public void initializeGridSearch() {
        if (secondRun == true && droneToLand.missionToLand()){
            this.droneGridSearch = new GridSearch(droneToLand.uTurnDirection(), droneToLand.getCurrentDirection());
            secondRun = false;
        }
    }

    public JSONObject makeDecision() {
        
        JSONObject decision = new JSONObject();
        data.initializeExtras(currentInformation);

        if (firstRun){
            droneToLand.getInfo(currentInformation);
            String forward = droneToLand.getCurrentDirection().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
            firstRun = false;
        } else if (!droneToLand.missionToLand()) {
            droneToLand.getInfo(currentInformation);
            decision = droneToLand.makeDecision();
        } else if (droneToLand.missionToLand()) {
            initializeGridSearch();
            droneGridSearch.getInfo(currentInformation);
            decision = droneGridSearch.makeDecision();
        } else {
            decision = decision.put("action", "stop");
        }

        if (batteryLevelWarning()){
            decision = decision.put("action", "stop");
        }

        return decision;
    }
    
}