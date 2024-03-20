package ca.mcmaster.se2aa4.island.team219;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MakeDecision {

    public Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Battery batteryLevel; 
    private Compass currentDirection;
    private boolean firstRun;
    private GoToLand droneToLand;
    private GridSearch droneGridSearch;
    private boolean secondRun;
    private final Logger logger = LogManager.getLogger();

    public MakeDecision(Integer battery, Compass direction) {
        this.currentDirection = direction;
        int batteryInt = battery.intValue(); 
        this.batteryLevel = new Battery(batteryInt);
        data = new AcknowledgeResults();
        this.firstRun = true;
        this.secondRun = true;
        this.droneToLand = new GoToLand(batteryLevel, currentDirection);
        
    }
    
    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }

    public int getBatteryLevelDrone() {
        return this.batteryLevel.getBatteryLevel();
    }

    public String getClosestCreek(){
        return droneGridSearch.getClosestCreek();
    }

    public void initializeGridSearch() {
        if (secondRun == true && droneToLand.missionToLand()){
            this.droneGridSearch = new GridSearch(droneToLand.uTurnDirection(), droneToLand.echoeUntilOcean(), droneToLand.getBattery(), droneToLand.getCurrentDirection(), droneToLand.getOriginalXCoordinate(), droneToLand.getOriginalYCoordinate());
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

        logger.info(droneToLand.getCurrentDirection());

        return decision;
    }

}