package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class GoToLand extends Drone {
    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Battery batteryLevel; 
    private Compass currentDirection;
    private Compass temporaryDirection;
    private VirtualCoordinateMap map;
    private Compass oldDirection;

    private boolean echoAll;
    private boolean echoForward;
    private boolean echoRight;
    private boolean echoLeft;
    private boolean goToLand;
    private int echoCounter; 
    public boolean missionToLand; // also in make decision
    private int originalX; // also in make decision
    private int originalY; // also in make decision
    
    //Shared
    private boolean scanned;
    private String uTurnDirection;
    private String echoeUntilOcean;
    public int distanceToLand;

    public boolean dontEchoRight;

    public boolean dontEchoLeft;

    public int range;

    public boolean turnedLeft;

    public boolean turnedRight;

    private ToLandState state;


    public GoToLand(Battery batteryLevel, Compass direction) {
        this.currentDirection = direction;
        this.batteryLevel = batteryLevel;
        this.map = new VirtualCoordinateMap(currentDirection, 0, 0); 
        data = new AcknowledgeResults();
    
        this.echoAll = false; 
        this.echoForward = false;
        this.echoLeft = false;
        this.echoRight = false; 
        this.goToLand = false;
        this.missionToLand = false;
        this.echoCounter = 0;
        this.uTurnDirection = "left";
        this.echoeUntilOcean = "left";
        this.originalX = 0;
        this.originalY = 0;

        this.range = 0;
        this.state = new EchoForwardState();
        this.dontEchoLeft = false;
        this.dontEchoRight = false;
        this.turnedLeft = false;
        this.turnedRight = false;

        
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
    }

    @Override
    public int getBatteryLevelDrone() {
        return this.batteryLevel.getBatteryLevel();
    }

    @Override
    public boolean batteryLevelWarning(){
        if (getBatteryLevelDrone() <= 30){
            return true;
        }else{
            return false;
        }
    }

    public Battery getBattery() {
        return batteryLevel;
    }

    public int getOriginalXCoordinate() {
        return originalX;
    }

    public int getOriginalYCoordinate() {
        return originalY;
    }

    public String uTurnDirection() {
        return uTurnDirection;
    }

    public String echoeUntilOcean() {
        return echoeUntilOcean;
    }

    public boolean missionToLand() {
        return missionToLand;
    }

    public void switchState(ToLandState state) {
        this.state = state;
    }

    public boolean isFound(){
        data.initializeExtras(currentInformation);
        return data.isFound();
    }

    public boolean groundIsFound() {
        data.initializeExtras(currentInformation);
        return data.groundIsFound();
    }

    public Compass getCurrentDirection() {
        return currentDirection;
    }

    public int distanceUntilLand() {
        data.initializeExtras(currentInformation);
        range = data.distance();
        return range;
    }

    public void setRange(int distance) {
        range = distance;
    }

    public int getRange() {
        return range;
    }


    public JSONObject turnLeftToLand() {
        turnedLeft = true;
        return turnLeft(currentDirection);
    }

    public JSONObject turnRightToLand() {
        turnedRight = true;
        return turnRight(currentDirection);
    }

    public JSONObject scanToLand() {
        return scan();
    }
    

    @Override
    public JSONObject makeDecision() {
        JSONObject decision = new JSONObject();

        data.initializeExtras(currentInformation);

        decision = state.stateChange(this, currentInformation);

        if (decision.toString().contains("fly")){
            map.moveForward();
            
        } else if (decision.toString().contains("heading")){
            if (turnedRight == true){
                currentDirection = currentDirection.right();
                map.turnRight();
                turnedRight = false;
            } else if (turnedLeft == true){
                currentDirection = currentDirection.left();
                map.turnLeft();
                turnedLeft = false;
            }
            
        }

        if (batteryLevelWarning()){
            decision = stop();
        }


        if (turnedRight){ 
            uTurnDirection = "right";
            echoeUntilOcean = "left";
        }else{
            uTurnDirection = "left";
            echoeUntilOcean = "right";
        }

        return decision;
    }
}