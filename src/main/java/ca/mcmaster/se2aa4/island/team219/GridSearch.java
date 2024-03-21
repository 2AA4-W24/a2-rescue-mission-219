package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class GridSearch extends Drone{

    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Compass currentDirection;
    private VirtualCoordinateMap map;
    private GridSearchState state;
    private String uTurnDirection;
    private int outOfRangeCounter;
    private int islandHalvesExplored; 
    private int uTurns;
    private int originalX;
    private int originalY;
    private int creekX;
    private int creekY;
    private int emergencySiteCoordinatesX;
    private int emergencySiteCoordinatesY;
    private int range;
    private int distanceToOOB;
    private boolean firstRun;
    private boolean checkedForSite;
    private boolean turnedRight;
    private boolean turnedLeft;

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
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
    }
    
    public void switchState(GridSearchState state) {
        this.state = state;
    }


    private JSONObject turnLeftGridSearch() {
        turnedLeft = true;
        return turnLeft(currentDirection);
    }

    private JSONObject turnRightGridSearch() {
        turnedRight = true;
        return turnRight(currentDirection);
    }

    private JSONObject scanGridSearch() {
        checkedForSite = true;
        return scan();
    }

    public String getClosestCreek() {
        return data.getClosestCreek();
    }

    @Override
    public JSONObject makeDecision() {

        JSONObject decision = new JSONObject();
        data.initializeExtras(currentInformation);

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

        decision = state.stateChange(this);
        
        if (decision.toString().contains("fly")) {
            map.moveForward();
        } else if (decision.toString().contains("heading")) {

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

        if (decision.toString().contains("scan") && firstRun) {
            originalX = map.getCurrentX();
            originalY = map.getCurrentY();
            firstRun = false;
        } 

        if (islandHalvesExplored > 1 & originalX == map.getCurrentX() && originalY == map.getCurrentY()) {
            decision = stop();
        }
        
        return decision;
    }

    private class BigUTurnState implements GridSearchState {

    
        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
            
            if (drone.uTurnDirection.equals("right")) {
                
                if (drone.uTurns == 0) {
                    decision = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                    drone.islandHalvesExplored++;
                } else if (drone.uTurns == 1) {
                    decision = fly();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 2) {
                    decision = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 3) {
                    decision = drone.scanGridSearch();
                    drone.switchState(new ScanState());
                    drone.checkedForSite = true;
                    drone.uTurns = 0;
                    drone.uTurnDirection = "right";
                    drone.islandHalvesExplored++;
                }
    
            } else if (drone.uTurnDirection.equals("left")) {
    
                if (drone.uTurns == 0) {
                    decision = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                    drone.islandHalvesExplored++;
                } else if (drone.uTurns == 1) {
                    decision = fly();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 2) {
                    decision = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 3) {
                    decision = drone.scanGridSearch();
                    drone.checkedForSite = true;
                    drone.switchState(new ScanState());
                    drone.uTurns = 0;
                    drone.uTurnDirection = "left";
                }
    
            }
            
            return decision;
        } 
    }

    private class EchoState implements GridSearchState {

    
        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
            
            if (data.isFound()) {
                drone.switchState(new FlyState());
                decision = fly();
                int distance = data.distance();
                distance = distance - 1;
                drone.range = distance;
                drone.outOfRangeCounter= 0;
            } else if (!data.isFound()) {
                decision = echoTowards(currentDirection);
                drone.outOfRangeCounter++;
                drone.switchState(new SecondEchoState());
            }
            
            return decision;
        } 
    }    

    public class FlyState implements GridSearchState {

        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
    
            if (drone.range > 0) {
                drone.switchState(new FlyState());
                decision = fly();
                drone.range--;
            } else {
                drone.switchState(new ScanState());
                decision = drone.scanGridSearch();
                drone.checkedForSite = true;
            }
            
            return decision;
        } 
        
    }

    public class FlyToEndState implements GridSearchState {
    
        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
    
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                decision = fly();
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                decision = fly();
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB <= 7) {
                decision = fly();
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
    
            if (drone.distanceToOOB < 2) {
                decision = echoTowards(currentDirection);
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
            
            return decision;
        } 
    
    }
    
    public class FlyToUTurnState implements GridSearchState {
    
        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
    
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                decision = fly();
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                decision = fly();
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB <= 7) {
                decision = fly();
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
    
            if (drone.distanceToOOB < 2) {
                decision = echoTowards(currentDirection);
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
            
            return decision;
        } 
        
    }
    
    public class ScanState implements GridSearchState {

        private AcknowledgeResults data = new AcknowledgeResults();
    
        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            
            data.initializeExtras(currentInformation);
            
            if (data.groundIsFound()){
                drone.switchState(new FlyState());
                decision = fly();
                drone.outOfRangeCounter = 0;
            } else if (!data.groundIsFound()) {
                drone.switchState(new EchoState());
                decision = echoTowards(currentDirection);
            }
    
            return decision;
        } 
        
    }
    
    public class SecondEchoState implements GridSearchState {

        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
            
            if (drone.outOfRangeCounter == 2) {
                drone.switchState(new FlyToEndState());
                decision = echoTowards(currentDirection);
            } else {
                drone.switchState(new FlyToUTurnState());
                decision = echoTowards(currentDirection);
            }
            
            return decision;
        } 
        
    }
    
    public class UTurnState implements GridSearchState {
    
        @Override
        public JSONObject stateChange(GridSearch drone) {
            
            JSONObject decision = new JSONObject();
            data.initializeExtras(currentInformation);
    
            if (drone.uTurnDirection == "left") {
    
                if (drone.uTurns == 0) {
                    decision = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 1) {
                    decision = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 2) {
                    decision = echoTowards(currentDirection);
                    drone.uTurns = 0;
                    drone.uTurnDirection = "right";
                    drone.switchState(new EchoState());
                }
    
            } else if (drone.uTurnDirection == "right") {
    
                if (drone.uTurns == 0) {
                    decision = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 1) {
                    decision = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 2) {
                    decision = echoTowards(currentDirection);
                    drone.uTurns = 0;
                    drone.uTurnDirection = "left";
                    drone.switchState(new EchoState());
                }
    
            }
            
            return decision;
        } 
    
    }
    
}