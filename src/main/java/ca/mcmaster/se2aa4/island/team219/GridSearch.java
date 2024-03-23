package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class GridSearch implements DecisionMaker{

    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Compass currentDirection;
    private VirtualCoordinateMap map;
    private GridSearchState state;
    public String uTurnDirection;
    public int outOfRangeCounter;
    public int islandHalvesExplored; 
    public int uTurns;
    public int originalX;
    public int originalY;
    public int range;
    public int distanceToOOB;
    public boolean firstRun;
    public boolean checkedForSite;
    public boolean turnedRight;
    public boolean turnedLeft;
    public DroneCommands droneCommand;

    public GridSearch(String uTurnDirection, Compass direction) {
        this.state = new ScanState();
        data = new AcknowledgeResults();
        this.uTurnDirection = uTurnDirection;
        this.currentDirection = direction;
        this.map = new VirtualCoordinateMap(currentDirection, originalX, originalY); 
        this.originalX = 0;
        this.originalY = 0;
        this.uTurns = 0;
        this.outOfRangeCounter = 0;
        this.islandHalvesExplored = 0;
        this.range = 0;
        this.distanceToOOB = 0;
        this.turnedLeft = false;
        this.turnedRight = false;
        this.firstRun = true;
        this.droneCommand = new DroneCommands();
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
    }
    
    public GridSearchState getState() {
        return state;
    }
    
    public void setData(AcknowledgeResults data) {
        this.data = data;
    }
    
    public void switchState(GridSearchState state) {
        this.state = state;
    }

    private Commands turnLeftGridSearch() {
        turnedLeft = true;
        return droneCommand.turnLeft(currentDirection);
    }

    private Commands turnRightGridSearch() {
        turnedRight = true;
        return droneCommand.turnRight(currentDirection);
    }

    private Commands scanGridSearch() {
        checkedForSite = true;
        return droneCommand.scan();
    }


    @Override
    public String getClosestCreek() {
        return data.getClosestCreek();
    }

    @Override
    public String uTurnDirection() {
        return uTurnDirection;
    }

    @Override
    public boolean missionToLand() {
        return false;
    }

    @Override
    public Compass getCurrentDirection() {
        return currentDirection;
    }

    @Override
    public Commands makeDecision() {

        Commands command;

        data.initializeExtras(currentInformation);

        int creekX;
        int creekY;
        int emergencySiteCoordinatesX;
        int emergencySiteCoordinatesY;

        if (checkedForSite) {

            if (data.creekIsFound()) {
                creekX = map.getCurrentX();
                creekY = map.getCurrentY();
                data.storeCoordinates(creekX,creekY);
            }
            if (data.emergencySiteIsFound()) {
                emergencySiteCoordinatesX = map.getCurrentX();
                emergencySiteCoordinatesY = map.getCurrentY();
                data.storeCoordinatesEmergency(emergencySiteCoordinatesX, emergencySiteCoordinatesY);
            }

            checkedForSite = false;
        }

        command = state.stateChange(this);
        
        if (command.getValue().equals("fly")) {
            map.moveForward();
        } else if (command.getValue().equals("heading")) {

            if (turnedRight) {
                currentDirection = currentDirection.right();
                map.turnRight();
                turnedRight = false;
            } else if (turnedLeft) {
                currentDirection = currentDirection.left();
                map.turnLeft();
                turnedLeft = false;
            }

        }

        if (command.getValue().equals("scan") && firstRun) {
            originalX = map.getCurrentX();
            originalY = map.getCurrentY();
            firstRun = false;
        } 

        if (islandHalvesExplored > 1 && originalX == map.getCurrentX() && originalY == map.getCurrentY()) {
            command = droneCommand.stop();
        }
        
        return command;
    }

    class BigUTurnState implements GridSearchState {

        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command = new Commands("");
            data.initializeExtras(currentInformation);
            
            if (drone.uTurnDirection.equals("right")) {
                
                if (drone.uTurns == 0) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                    drone.islandHalvesExplored++;
                } else if (drone.uTurns == 1) {
                    command = droneCommand.fly();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 2) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 3) {
                    command = drone.scanGridSearch();
                    drone.switchState(new ScanState());
                    drone.checkedForSite = true;
                    drone.uTurns = 0;
                    drone.uTurnDirection = "right";
                    drone.islandHalvesExplored++;
                }
    
            } else if (drone.uTurnDirection.equals("left")) {
    
                if (drone.uTurns == 0) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                    drone.islandHalvesExplored++;
                } else if (drone.uTurns == 1) {
                    command = droneCommand.fly();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 2) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 3) {
                    command = drone.scanGridSearch();
                    drone.checkedForSite = true;
                    drone.switchState(new ScanState());
                    drone.uTurns = 0;
                    drone.uTurnDirection = "left";
                }
    
            }
            
            return command;
        } 
    }

    class EchoState implements GridSearchState {

        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command = new Commands("");
            data.initializeExtras(currentInformation);
            
            if (data.isFound()) {
                drone.switchState(new FlyState());
                command = droneCommand.fly();
                int distance = data.distance();
                distance = distance - 1;
                drone.range = distance;
                drone.outOfRangeCounter= 0;
            } else if (!data.isFound()) {
                command = droneCommand.echoTowards(currentDirection);
                drone.outOfRangeCounter++;
                drone.switchState(new SecondEchoState());
            }
            
            return command;
        } 
    }    

    class FlyState implements GridSearchState {

        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command;
            data.initializeExtras(currentInformation);
    
            if (drone.range > 0) {
                drone.switchState(new FlyState());
                command = droneCommand.fly();
                drone.range--;
            } else {
                drone.switchState(new ScanState());
                command = drone.scanGridSearch();
                drone.checkedForSite = true;
            }
            
            return command;
        } 
        
    }

    class FlyToEndState implements GridSearchState {

        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command = new Commands("");
            data.initializeExtras(currentInformation);
    
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                command = droneCommand.fly();
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                command = droneCommand.fly();
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB <= 7) {
                command = droneCommand.fly();
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
    
            if (drone.distanceToOOB < 2) {
                command = droneCommand.echoTowards(currentDirection);
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
            
            return command;
        } 
    
    }
    
    class FlyToUTurnState implements GridSearchState {
    
        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command = new Commands("");
            data.initializeExtras(currentInformation);
    
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                command = droneCommand.fly();
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                command = droneCommand.fly();
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB <= 7) {
                command = droneCommand.fly();
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
    
            if (drone.distanceToOOB < 2) {
                command = droneCommand.echoTowards(currentDirection);
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
            
            return command;
        } 
        
    }
    
    class ScanState implements GridSearchState {

        private AcknowledgeResults data = new AcknowledgeResults();
    
        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command = new Commands("");
            data.initializeExtras(currentInformation);
            
            if (data.groundIsFound()){
                drone.switchState(new FlyState());
                command = droneCommand.fly();
                drone.outOfRangeCounter = 0;
            } else if (!data.groundIsFound()) {
                drone.switchState(new EchoState());
                command = droneCommand.echoTowards(currentDirection);
            }
    
            return command;
        } 
        
    }
    
    class SecondEchoState implements GridSearchState {

        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command;
            data.initializeExtras(currentInformation);
            
            if (drone.outOfRangeCounter == 2) {
                drone.switchState(new FlyToEndState());
                command = droneCommand.echoTowards(currentDirection);
            } else {
                drone.switchState(new FlyToUTurnState());
                command = droneCommand.echoTowards(currentDirection);
            }
            
            return command;
        } 
        
    }
    
    class UTurnState implements GridSearchState {

        @Override
        public Commands stateChange(GridSearch drone) {
            Commands command = new Commands("");
            data.initializeExtras(currentInformation);
    
            if (drone.uTurnDirection == "left") {
    
                if (drone.uTurns == 0) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 1) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 2) {
                    command = droneCommand.echoTowards(currentDirection);
                    drone.uTurns = 0;
                    drone.uTurnDirection = "right";
                    drone.switchState(new EchoState());
                }
    
            } else if (drone.uTurnDirection == "right") {
    
                if (drone.uTurns == 0) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 1) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 2) {
                    command = droneCommand.echoTowards(currentDirection);
                    drone.uTurns = 0;
                    drone.uTurnDirection = "left";
                    drone.switchState(new EchoState());
                }
    
            }
            
            return command;
        } 
    
    }
    
}