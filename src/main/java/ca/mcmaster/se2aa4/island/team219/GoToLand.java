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
    private boolean missionToLand; // also in make decision
    private int originalX; // also in make decision
    private int originalY; // also in make decision
    
    //Shared
    private boolean scanned;
    private String uTurnDirection;
    private String echoeUntilOcean;
    private int distanceToLand;


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

    public Compass getCurrentDirection() {
        return currentDirection;
    }



    
    public JSONObject echoInAllDirections() {


        JSONObject decision = new JSONObject();

        
        if (!echoForward) {
            decision = echoTowards(currentDirection);
            this.echoForward = true;
            this.temporaryDirection = this.currentDirection;
        } else if (!echoRight) {
            decision = echoRight(currentDirection);
            this.echoRight = true;
            this.temporaryDirection = this.currentDirection.right();
        } else if (!echoLeft) {
            decision = echoLeft(currentDirection);
            this.echoLeft = true;
            this.temporaryDirection = this.currentDirection.left();
        }
    
        if (echoForward && echoRight && echoLeft) {
            echoAll = true;
        } 

        return decision;
    }

    
    public JSONObject turn(Compass direction) {
        JSONObject decision = new JSONObject();
        oldDirection = currentDirection;
        currentDirection = direction;
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        
        if (currentDirection == oldDirection.right()) { 
            map.turnRight(); 
            
        } else if (currentDirection == oldDirection.left()) {
            map.turnLeft(); 
        }

        return decision;
    }

    @Override
    public JSONObject makeDecision() {
        JSONObject decision = new JSONObject();

        data.initializeExtras(currentInformation);

        if (echoCounter == 1){
            distanceToLand = data.distance();
        }

        if (data.isFound() && echoCounter == 0) {
            distanceToLand = data.distance();
            this.goToLand = true;
            if (currentDirection == temporaryDirection){
                decision = fly();
            }else{
                decision = turn(temporaryDirection);
            }
            
            if (temporaryDirection == currentDirection.left() || currentDirection == temporaryDirection){ 
                uTurnDirection = "left";
                echoeUntilOcean = "right";
            }else{
                uTurnDirection = "right";
                echoeUntilOcean = "left";
            }

        } else if (!echoAll && !goToLand && !data.isFound()){
            decision = echoInAllDirections();

        } else if (echoAll && !goToLand) {
            decision = fly();
            echoAll = false;
            echoRight = false;
            echoLeft = false;

        } else if (goToLand) {
            if (distanceToLand != 0){
                if (echoCounter == 0){
                    decision = echoTowards(currentDirection);
                    echoCounter++;
                } else if (echoCounter == 1){
                    decision = fly();
                }
                scanned = false;
                distanceToLand--;
            } else if (distanceToLand == 0 && scanned == false) {
                decision = scan();
                scanned = true;
                missionToLand = true;
                echoRight = false;
                echoLeft = false;
                originalX = map.getCurrentX();
                originalY = map.getCurrentY();
            }
        }

        if (batteryLevelWarning()){
            decision = stop();
        }

        return decision;
    }
}
