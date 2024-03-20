package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class GridSearch extends Drone{

    public Information currentInformation = new Information(0, new JSONObject());
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
    public boolean askedForRange;
    public boolean firstScan;
    private State state;
    public int flyCounter;
    public int distanceToOOB;

    

    public GridSearch(String uTurnDirection, String echoeUntilOcean, Battery batteryLevel, Compass direction, int originalX, int originalY) {
        this.currentDirection = direction;
        this.batteryLevel = batteryLevel;
        this.map = new VirtualCoordinateMap(currentDirection, originalX, originalY); 
        data = new AcknowledgeResults();
        this.originalX = originalX;
        this.originalY = originalY;
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
        this.askedForRange = false;
        this.firstScan = true;
        this.flyCounter = 0;
        this.distanceToOOB = 0;
        
        
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
    }
    
    public void switchState(State state) {
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
        if (getBatteryLevelDrone() <= 30){
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

}