package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class FindLand implements DecisionMaker{
    
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
    private DroneCommands droneCommand;

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
        this.droneCommand = new DroneCommands();
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
    }

    @Override
    public String uTurnDirection() {
        return uTurnDirection;
    }

    @Override
    public boolean missionToLand() {
        return missionToLand;
    }

    @Override
    public Compass getCurrentDirection() {
        return currentDirection;
    }

    private void switchState(FindLandState state) {
        this.state = state;
    }


    private Commands turnLeftToLand() {
        turnedLeft = true;
        return droneCommand.turnLeft(currentDirection);
    }

    private Commands turnRightToLand() {
        turnedRight = true;
        return droneCommand.turnRight(currentDirection);
    }


    @Override
    public String getClosestCreek() {
        return data.getClosestCreek();
    }
    
    @Override
    public Commands makeDecision() {

        Commands command;

        data.initializeExtras(currentInformation);
        command = state.stateChange(this);

        if (command.getValue().equals("heading")) {

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

        return command;
    }

    
    private class StartingState implements FindLandState {

        private Commands command;

        @Override
        public Commands stateChange(FindLand drone) {
            data.initializeExtras(currentInformation);

            command = droneCommand.echoTowards(currentDirection);
            drone.switchState(new EchoForwardState());
            return command;
        } 

    }

    private class EchoForwardState implements FindLandState {
    
        private Commands command;

        @Override
        public Commands stateChange(FindLand drone) {
            
            data.initializeExtras(currentInformation);
    
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                command = droneCommand.fly();
            } else if (!data.isFound()) {
                drone.switchState(new EchoRightState());
                command = droneCommand.echoTowards(currentDirection.right());
            }
            
            return command;
        } 
        
    }

    private class EchoRightState implements FindLandState {
    
        private Commands command;

        @Override
        public Commands stateChange(FindLand drone) {

            data.initializeExtras(currentInformation);
    
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                command = drone.turnRightToLand();
            } else if (!data.isFound()) {
    
                if (data.distance() <= 2) {
                    drone.dontEchoRight = true;
                }
    
                drone.switchState(new EchoLeftState());
                command = droneCommand.echoTowards(currentDirection.left()); 
    
                if (drone.dontEchoLeft) {
                    drone.switchState(new FlyForwardState());
                    command = droneCommand.fly();
                }
            }
            
            return command;
        } 
    
    }

    private class EchoLeftState implements FindLandState {

        private Commands command;
    
        @Override
        public Commands stateChange(FindLand drone) {
    
            data.initializeExtras(currentInformation);
    
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                command = drone.turnLeftToLand();
            } else if (!data.isFound()) {
    
                if (data.distance() <= 2) {
                    drone.dontEchoLeft = true;
                }
    
                drone.switchState(new FlyForwardState());
                command = droneCommand.fly();
    
            }
            
            return command;
        } 
    
    }

    private class FlyForwardState implements FindLandState {
    
        private Commands command;

        @Override
        public Commands stateChange(FindLand drone) {
            
            data.initializeExtras(currentInformation);
    
            if (drone.dontEchoRight && !drone.dontEchoLeft) {
                drone.switchState(new EchoLeftState());
                command = droneCommand.echoTowards(currentDirection.left());
            } else {
                drone.switchState(new EchoRightState());
                command = droneCommand.echoTowards(currentDirection.right());
            }
            
            return command;
        } 
    
    }

    private class TurnToLandState implements FindLandState {

        private Commands command;

        @Override
        public Commands stateChange(FindLand drone) {

            data.initializeExtras(currentInformation);

            command = droneCommand.scan();
            drone.missionToLand = true;
            return command;
        } 
    
    }



}