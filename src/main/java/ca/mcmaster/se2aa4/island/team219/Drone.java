package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class Drone {

    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    protected Battery batteryLevel;  //made protected for JUnit testing purposes
    private Compass currentDirection; 
    protected DecisionMaker findLandDecisionMaker;  //made protected for JUnit testing purposes
    protected DecisionMaker gridSearchDecisionMaker;  //made protected for JUnit testing purposes
    protected boolean firstRun;  //made protected for JUnit testing purposes
    protected boolean secondRun;  //made protected for JUnit testing purposes
    protected DroneCommands droneCommand;  //made protected for JUnit testing purposes

    public Drone(Integer battery, Compass direction) {
        this.currentDirection = direction;
        int batteryInt = battery.intValue(); 
        this.batteryLevel = new Battery(batteryInt);
        this.firstRun = true;
        this.secondRun = true;
        this.findLandDecisionMaker = new FindLand(currentDirection); 
        data = new AcknowledgeResults();
        this.droneCommand = new DroneCommands();
    }

    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }

    public int getBatteryLevelDrone() {
        return this.batteryLevel.getBatteryLevel();
    }
    
    public boolean batteryLevelWarning(){
        return getBatteryLevelDrone() <= 40;
    }

    public String getClosestCreek(){
        return gridSearchDecisionMaker.getClosestCreek();
    }

    public void initializeGridSearch() {
        if (secondRun && findLandDecisionMaker.missionToLand()){
            this.gridSearchDecisionMaker = new GridSearch(findLandDecisionMaker.uTurnDirection(), findLandDecisionMaker.getCurrentDirection());
            secondRun = false;
        }
    }


    public Commands makeDecision() {
        
        Commands command;
        data.initializeExtras(currentInformation);

        if (firstRun){
            findLandDecisionMaker.getInfo(currentInformation);
            command = droneCommand.echoTowards(currentDirection);
            firstRun = false;
        } else if (!findLandDecisionMaker.missionToLand()) {
            findLandDecisionMaker.getInfo(currentInformation);
            command = findLandDecisionMaker.makeDecision();
        } else if (findLandDecisionMaker.missionToLand()) {
            initializeGridSearch();
            gridSearchDecisionMaker.getInfo(currentInformation);
            command = gridSearchDecisionMaker.makeDecision();
        } else {
            command = droneCommand.stop();
        }

        if (batteryLevelWarning()){
            command = droneCommand.stop();
        }

        return command;
    }

}
