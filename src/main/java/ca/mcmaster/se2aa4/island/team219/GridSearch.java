package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class GridSearch extends Drone{

    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Battery batteryLevel; 
    private Compass currentDirection;
    private VirtualCoordinateMap map;
    private boolean bigUTurnComplete; 
    public int outOfRangeCounter;
    public int islandHalvesExplored; 
    public int uTurns;
    public String uTurnDirection;
    public boolean checkedForSite;
    private boolean turnedRight;
    private boolean turnedLeft;
    private int originalX;
    private int originalY;
    private int creekX;
    private int creekY;
    private int emergencySiteCoordinatesX;
    private int emergencySiteCoordinatesY;
    private boolean firstRun;
    public boolean echoFoundGround;
    public boolean scanFoundGround;
    public int range;
    public boolean firstScan;
    private GridSearchState state;
    public int flyCounter;
    public int distanceToOOB;

    public GridSearch(String uTurnDirection, Battery batteryLevel, Compass direction) {
        this.currentDirection = direction;
        this.batteryLevel = batteryLevel;
        this.map = new VirtualCoordinateMap(currentDirection, originalX, originalY); 
        this.originalX = 0;
        this.originalY = 0;
        this.uTurnDirection = uTurnDirection;
        this.uTurns = 0;
        this.outOfRangeCounter = 0;
        this.bigUTurnComplete = true;
        this.islandHalvesExplored = 0;
        this.turnedLeft = false;
        this.turnedRight = false;
        this.firstRun = true;
        this.range = 0;
        this.state = new ScanState();
        this.echoFoundGround = false;
        this.firstScan = true;
        this.flyCounter = 0;
        this.distanceToOOB = 0;
        data = new AcknowledgeResults();
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
    }
    
    public void switchState(GridSearchState state) {
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

    public JSONObject turnLeftGridSearch() {
        turnedLeft = true;
        return turnLeft(currentDirection);
    }

    public JSONObject turnRightGridSearch() {
        turnedRight = true;
        return turnRight(currentDirection);
    }

    public JSONObject scanGridSearch() {
        checkedForSite = true;
        return scan();
    }

    public int islandHalvesExplored() {
        return islandHalvesExplored;
    }

    public boolean bigUTurnComplete() {
        return bigUTurnComplete;
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

        decision = state.stateChange(this, currentInformation);
        
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

        if (batteryLevelWarning()){
            decision = stop();
        }

        if (islandHalvesExplored > 1 & originalX == map.getCurrentX() && originalY == map.getCurrentY()) {
            decision = stop();
        }
        
        return decision;
    }

    private class BigUTurnState implements GridSearchState {

        private AcknowledgeResults data = new AcknowledgeResults();
        private Information info = new Information(0, new JSONObject());
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
            
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
            
            if (drone.uTurnDirection.equals("right")) {
                
                if (drone.uTurns == 0) {
                    decision = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                    drone.islandHalvesExplored++;
                } else if (drone.uTurns == 1) {
                    decision = decision.put("action", "fly");
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
                    decision = decision.put("action", "fly");
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

        private AcknowledgeResults data = new AcknowledgeResults();
        private Information info = new Information(0, new JSONObject());
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
            
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
            
            if (data.isFound()) {
                drone.switchState(new FlyState());
                decision = decision.put("action", "fly");
                int distance = drone.distanceUntilLand();
                distance = distance - 1;
                drone.setRange(distance);
                drone.outOfRangeCounter= 0;
            } else if (!data.isFound()) {
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
                drone.outOfRangeCounter++;
                drone.switchState(new SecondEchoState());
            }
            
            return decision;
        } 
    }    

    public class FlyState implements GridSearchState {

        private Information info = new Information(0, new JSONObject());
        private AcknowledgeResults data = new AcknowledgeResults();
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
    
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
    
            if (drone.getRange() > 0) {
                drone.switchState(new FlyState());
                decision = decision.put("action", "fly");
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

        private Information info = new Information(0, new JSONObject());
        private AcknowledgeResults data = new AcknowledgeResults();
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
    
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
    
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                decision = decision.put("action", "fly");
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                decision = decision.put("action", "fly");
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB <= 7) {
                decision = decision.put("action", "fly");
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
    
            if (drone.distanceToOOB < 2) {
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
            
            return decision;
        } 
    
    }
    
    public class FlyToUTurnState implements GridSearchState {

        private Information info = new Information(0, new JSONObject());
        private AcknowledgeResults data = new AcknowledgeResults();
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
    
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
    
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                decision = decision.put("action", "fly");
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                decision = decision.put("action", "fly");
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB <= 7) {
                decision = decision.put("action", "fly");
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
    
            if (drone.distanceToOOB < 2) {
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
            
            return decision;
        } 
        
    }
    
    public class ScanState implements GridSearchState {

        private Information info = new Information(0, new JSONObject());
        private AcknowledgeResults data = new AcknowledgeResults();
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
            
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
            
            if (data.groundIsFound()){
                drone.switchState(new FlyState());
                decision = decision.put("action", "fly");
                drone.outOfRangeCounter = 0;
            } else if (!data.groundIsFound()) {
                drone.switchState(new EchoState());
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
            }
    
            return decision;
        } 
        
    }
    
    public class SecondEchoState implements GridSearchState {

        private AcknowledgeResults data = new AcknowledgeResults();
        private Information info = new Information(0, new JSONObject());
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
            
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
            
            if (drone.outOfRangeCounter == 2) {
                drone.switchState(new FlyToEndState());
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
            } else {
                drone.switchState(new FlyToUTurnState());
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
            }
            
            return decision;
        } 
        
    }
    
    public class UTurnState implements GridSearchState {

        private Information info = new Information(0, new JSONObject());
        private AcknowledgeResults data = new AcknowledgeResults();
    
        @Override
        public JSONObject stateChange(GridSearch drone, Information currentInformation) {
            
            JSONObject decision = new JSONObject();
            this.info = currentInformation;
            data.initializeExtras(info);
    
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
                    String forward = drone.getCurrentDirection().toString();
                    JSONObject forwardJ = new JSONObject().put("direction", forward);
                    decision.put("action", "echo").put("parameters", forwardJ);
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
                    String forward = drone.getCurrentDirection().toString();
                    JSONObject forwardJ = new JSONObject().put("direction", forward);
                    decision.put("action", "echo").put("parameters", forwardJ);
                    drone.uTurns = 0;
                    drone.uTurnDirection = "left";
                    drone.switchState(new EchoState());
                }
    
            }
            
            return decision;
        } 
    
    }
    
}