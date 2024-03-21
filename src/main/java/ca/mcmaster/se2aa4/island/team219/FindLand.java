package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class FindLand extends Drone {
    
    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Compass currentDirection;
    private String uTurnDirection;
    private FindLandState state;
    private int distanceToLand;
    private boolean dontEchoRight;
    private boolean dontEchoLeft;
    private boolean turnedLeft;
    private boolean turnedRight;
    private boolean missionToLand; 

    public FindLand(Compass direction) {
        this.currentDirection = direction;
        this.missionToLand = false;
        this.uTurnDirection = "left";
        this.state = new StartingState();
        this.dontEchoLeft = false;
        this.dontEchoRight = false;
        this.turnedLeft = false;
        this.turnedRight = false;
        data = new AcknowledgeResults();
        this.distanceToLand = 0;
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
    }

    public String uTurnDirection() {
        return uTurnDirection;
    }

    public boolean missionToLand() {
        return missionToLand;
    }

    public Compass getCurrentDirection() {
        return currentDirection;
    }

    private void switchState(FindLandState state) {
        this.state = state;
    }


    private JSONObject turnLeftToLand() {
        turnedLeft = true;
        return turnLeft(currentDirection);
    }

    private JSONObject turnRightToLand() {
        turnedRight = true;
        return turnRight(currentDirection);
    }
    
    @Override
    public JSONObject makeDecision() {

        JSONObject decision = new JSONObject();
        data.initializeExtras(currentInformation);
        decision = state.stateChange(this);

        if (decision.toString().contains("heading")) {

            if (turnedRight == true) {
                currentDirection = currentDirection.right();
                turnedRight = false;
            } else if (turnedLeft == true) {
                currentDirection = currentDirection.left();
                turnedLeft = false;
            }

        }

        if (turnedRight) { 
            uTurnDirection = "right";
        } else {
            uTurnDirection = "left";
        }

        return decision;
    }

    
    private class StartingState implements FindLandState {

        @Override
        public JSONObject stateChange(FindLand drone) {
            JSONObject decision = new JSONObject();
            
            data.initializeExtras(currentInformation);

            decision = echoTowards(currentDirection);
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
                decision = echoRight(currentDirection);
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
                decision = echoLeft(currentDirection); 
    
                if (drone.dontEchoLeft) {
                    drone.switchState(new FlyForwardState());
                    decision = fly();
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
                decision = fly();
    
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
                decision = echoLeft(currentDirection);
            } else {
                drone.switchState(new EchoRightState());
                decision = echoRight(currentDirection);
            }
            
            return decision;
        } 
    
    }

    private class TurnToLandState implements FindLandState {

        @Override
        public JSONObject stateChange(FindLand drone) {

            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);

            decision = scan();
            drone.missionToLand = true;
            return decision;
        } 
    
    }



}