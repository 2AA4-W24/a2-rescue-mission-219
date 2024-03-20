package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class FindLand extends Drone {
    
    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Battery batteryLevel; 
    private Compass currentDirection;
    private VirtualCoordinateMap map;
    private int originalX; 
    private int originalY; 
    private String uTurnDirection;
    private String echoeUntilOcean;
    private int range;
    private boolean turnedLeft;
    private boolean turnedRight;
    private FindLandState state;
    public boolean missionToLand; 
    public int distanceToLand;
    public boolean dontEchoRight;
    public boolean dontEchoLeft;

    public FindLand(Battery batteryLevel, Compass direction) {
        this.currentDirection = direction;
        this.batteryLevel = batteryLevel;
        this.map = new VirtualCoordinateMap(currentDirection, 0, 0); 
        this.missionToLand = false;
        this.uTurnDirection = "left";
        this.echoeUntilOcean = "left";
        this.originalX = 0;
        this.originalY = 0;
        this.range = 0;
        this.state = new StartingState();
        this.dontEchoLeft = false;
        this.dontEchoRight = false;
        this.turnedLeft = false;
        this.turnedRight = false;
        data = new AcknowledgeResults();
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
        if (getBatteryLevelDrone() <= 40){
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

    public void switchState(FindLandState state) {
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
        decision = state.stateChange(this);

        if (decision.toString().contains("fly")) {
            map.moveForward();
        } else if (decision.toString().contains("heading")) {

            if (turnedRight == true) {
                currentDirection = currentDirection.right();
                map.turnRight();
                turnedRight = false;
            } else if (turnedLeft == true) {
                currentDirection = currentDirection.left();
                map.turnLeft();
                turnedLeft = false;
            }

        }

        if (batteryLevelWarning()) {
            decision = stop();
        }

        if (turnedRight) { 
            uTurnDirection = "right";
            echoeUntilOcean = "left";
        } else {
            uTurnDirection = "left";
            echoeUntilOcean = "right";
        }

        return decision;
    }

    
    private class StartingState implements FindLandState {

        @Override
        public JSONObject stateChange(FindLand drone) {
            JSONObject decision = new JSONObject();
            
            data.initializeExtras(currentInformation);

            String forward = drone.getCurrentDirection().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
            drone.switchState(new EchoForwardState());
            return decision;
        } 

    }

    private class EchoForwardState implements FindLandState {
    
        @Override
        public JSONObject stateChange(FindLand drone) {
    
            JSONObject decision = new JSONObject();
            
            data.initializeExtras(currentInformation);
    
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                decision.put("action", "fly");
            } else if (!data.isFound()) {
                drone.switchState(new EchoRightState());
                String forward = drone.getCurrentDirection().right().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
            }
            
            return decision;
        } 
        
    }

    private class EchoRightState implements FindLandState {
    
        @Override
        public JSONObject stateChange(FindLand drone) {
    
            JSONObject decision = new JSONObject();

            data.initializeExtras(currentInformation);
    
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                decision = drone.turnRightToLand();
            } else if (!data.isFound()) {
    
                if (data.distance() <= 2) {
                    drone.dontEchoRight = true;
                }
    
                drone.switchState(new EchoLeftState());
                String forward = drone.getCurrentDirection().left().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
    
                if (drone.dontEchoLeft) {
                    drone.switchState(new FlyForwardState());
                    decision.put("action", "fly");
                }
    
            }
            
            return decision;
        } 
    
    }

    private class EchoLeftState implements FindLandState {

    
        @Override
        public JSONObject stateChange(FindLand drone) {
    
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
    
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                decision = drone.turnLeftToLand();
            } else if (!data.isFound()) {
    
                if (data.distance() <= 2) {
                    drone.dontEchoLeft = true;
                }
    
                drone.switchState(new FlyForwardState());
                decision.put("action", "fly");
    
            }
            
            return decision;
        } 
    
    }

    private class FlyForwardState implements FindLandState {
    
        @Override
        public JSONObject stateChange(FindLand drone) {
    
            JSONObject decision = new JSONObject();
            
            data.initializeExtras(currentInformation);
    
            if (drone.dontEchoRight && !drone.dontEchoLeft) {
                drone.switchState(new EchoLeftState());
                String forward = drone.getCurrentDirection().left().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
            } else {
                drone.switchState(new EchoRightState());
                String forward = drone.getCurrentDirection().right().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
            }
            
            return decision;
        } 
    
    }

    private class TurnToLandState implements FindLandState {

        @Override
        public JSONObject stateChange(FindLand drone) {

            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);

            decision = drone.scanToLand();
            drone.missionToLand = true;
            return decision;
        } 
    
    }



}