package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class GridSearch extends Drone{
    private Information currentInformation = new Information(0, new JSONObject());
    private AcknowledgeResults data;
    private Battery batteryLevel; 
    private Compass currentDirection;
    private Compass temporaryDirection;
    private VirtualCoordinateMap map;
    private boolean scanned;
    private boolean fly;
    private boolean echoed;
    private boolean uTurnComplete;
    private boolean bigUTurnComplete; 
    private int outOfRangeCounter;
    private int gridSearchDistance;
    private boolean checkDistance;
    private int bigUTurnCounter;
    private boolean exploreIsland; 
    private boolean passedLand; 
    private boolean echoedForward; 
    private int islandHalvesExplored; 
    private int uTurns;
    private boolean firstRowScan;
    private String uTurnDirection;
    private String echoeUntilOcean;
    private int distanceToLand;
    private boolean checkedForSite;
    private boolean turnedRight;
    private boolean turnedLeft;
    private int originalX;
    private int originalY;
    private int creekX;
    private int creekY;
    private int emergencySiteCoordinatesX;
    private int emergencySiteCoordinatesY;
    private boolean firstRun;

    public GridSearch(String uTurnDirection, String echoeUntilOcean, Battery batteryLevel, Compass direction, int originalX, int originalY) {
        this.scanned = true;
        this.currentDirection = direction;
        this.batteryLevel = batteryLevel;
        this.map = new VirtualCoordinateMap(currentDirection, originalX, originalY); 
        data = new AcknowledgeResults();
        this.originalX = originalX;
        this.originalY = originalY;
        this.uTurnDirection = uTurnDirection;
        this.echoeUntilOcean = echoeUntilOcean;
        this.uTurnComplete = true;
        this.fly = false;
        this.uTurns = 0;
        this.echoed = false;
        this.exploreIsland = false;
        this.outOfRangeCounter = 0;
        this.gridSearchDistance = 0;
        this.bigUTurnCounter = 0;
        this.checkDistance = false;
        this.bigUTurnComplete = true;
        this.passedLand = false;
        this.echoedForward = false;
        this.islandHalvesExplored = 0;
        this.firstRowScan = false;
        this.distanceToLand = 0;
        this.turnedLeft = false;
        this.turnedRight = false;
        this.firstRun = true;
        
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

        if (!exploreIsland && islandHalvesExplored == 0) {
            decision = scanLand();
        } else if (!bigUTurnComplete && islandHalvesExplored == 1){
            decision = bigUTurn();
        } else if (!exploreIsland && islandHalvesExplored == 1){
            decision = scanLand();
        } else if (!bigUTurnComplete && islandHalvesExplored == 2){
            decision = bigUTurn();
        } else if (!exploreIsland && islandHalvesExplored == 2){
            decision = scanLand();
        } else if (islandHalvesExplored == 3){
            decision = stop();
        }

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

    public JSONObject scanLand() {
        JSONObject decision = new JSONObject();

        data.initializeExtras(currentInformation);

        if (echoed & data.isFound() & outOfRangeCounter < 3){
            outOfRangeCounter = 0;
        }

        if (echoedForward == true && data.isFound()){
            gridSearchDistance = data.distance();
        }
        
        if (outOfRangeCounter >= 2 ){

            if (outOfRangeCounter == 2){
                fly = false;
                echoed = false;
                outOfRangeCounter ++;
            }

            if (fly & data.isFound() & outOfRangeCounter < 20){
                decision = fly();
                fly = false;
                echoed = false;
                outOfRangeCounter++;
            } else if (!fly && !echoed){
                if (uTurnDirection == "left"){
                    decision = echoRight(currentDirection);
                } else {
                    decision = echoLeft(currentDirection);
                }
                fly = true;
                echoed = true;
            } else if (echoed & (outOfRangeCounter >= 15 || !data.isFound())){
                exploreIsland = true;
                decision = scanGridSearch();
                bigUTurnComplete = false;
                outOfRangeCounter = 0;
                islandHalvesExplored++;
                firstRowScan = false;
                bigUTurnCounter = 0;
                fly = false;
                echoed = true;
            }
            

        } else if ( gridSearchDistance != 0){
            scanned = false;
            fly = true;
            outOfRangeCounter = 0;
            checkDistance = false;
            echoedForward = false;
            decision = fly();
            gridSearchDistance--;
        } else if (!uTurnComplete || (echoed && data.outOfBounds())){
            if (uTurnDirection == "left"){
                if (uTurns == 0) {
                    uTurnComplete = false;
                    decision = turnLeftGridSearch();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnLeftGridSearch();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scanGridSearch();
                    scanned = true;
                    uTurnComplete = true;
                    fly = false;
                    uTurns = 0;
                    echoed = false;
                    uTurnDirection = "right";
                    passedLand = false;
                }
            } else if (uTurnDirection == "right"){
                if (uTurns == 0) {
                    uTurnComplete = false;
                    decision = turnRightGridSearch();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnRightGridSearch();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scanGridSearch();
                    scanned = true;
                    uTurnComplete = true;
                    fly = false;
                    uTurns = 0;
                    echoed = false;
                    uTurnDirection = "left";
                    passedLand = false;
                }
            }
        } else if (scanned && !fly && data.groundIsFound()) {
            decision = fly();
            scanned = false;
            fly = true;
            checkDistance = false;
            outOfRangeCounter = 0; 
        } else if (!scanned && fly && !data.groundIsFound() &&!passedLand) {
            decision = scanGridSearch();
            scanned = true;
            fly = false;
        
        } else if (scanned && !data.groundIsFound()){
            decision = echoTowards(currentDirection);
            scanned = false;
            fly = false;
            echoed = true; 
            echoedForward = true;
        } else if (echoed && data.isFound()) {
            decision = fly();
            scanned = false;
            fly = true;
            outOfRangeCounter = 0;
            checkDistance = false;
            echoedForward = false;
            
        } else if (echoed && !data.isFound() && !checkDistance){
            if (islandHalvesExplored % 2 == 0){
                if (echoeUntilOcean == "left"){
                    decision = echoRight(currentDirection);
                } else if (echoeUntilOcean == "right"){
                    decision = echoLeft(currentDirection);
                }
            } else {
                if (echoeUntilOcean == "left"){
                    decision = echoRight(currentDirection);
                } else if (echoeUntilOcean == "right"){
                    decision = echoLeft(currentDirection);
                }
            }
            
            outOfRangeCounter++;
            checkDistance = true;
            passedLand = true;
            echoedForward = false;
        } else if (data.isFound() ){
            decision = fly();
            checkDistance = false;
            
        }else if ( echoed && data.isFound() && checkDistance){
            decision = fly();
            checkDistance = false;
        } else if ( echoed && !data.isFound() && checkDistance){
            if (uTurnDirection == "left"){
                if (uTurns == 0) {
                    uTurnComplete = false;
                    decision = turnLeftGridSearch();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnLeftGridSearch();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scanGridSearch();
                    scanned = true;
                    uTurnComplete = true;
                    fly = false;
                    uTurns = 0;
                    echoed = false;
                    uTurnDirection = "right";
                    passedLand = false;
                }
            } else if (uTurnDirection == "right"){
                if (uTurns == 0) {
                    uTurnComplete = false;
                    decision = turnRightGridSearch();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnRightGridSearch();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scanGridSearch();
                    scanned = true;
                    uTurnComplete = true;
                    fly = false;
                    uTurns = 0;
                    echoed = false;
                    uTurnDirection = "left";
                    passedLand = false;
                }
            }
            echoeUntilOcean = uTurnDirection;
            checkDistance = false;
        }

        return decision;
        
    }

    public JSONObject bigUTurn() {

        JSONObject decision = new JSONObject();
        data.initializeExtras(currentInformation);

        if (gridSearchDistance <= 4){
            decision = fly();
            gridSearchDistance--;
        } else {
            decision = scanGridSearch();
            bigUTurnComplete = true;
        }

        if (firstRowScan == true) {
            if (scanned && !fly && data.groundIsFound()) {
                decision = fly();
                scanned = false;
                fly = true;
            } else if (!scanned && fly && !data.groundIsFound()) {
                decision = scanGridSearch();
                scanned = true;
                fly = false;
            } else if (scanned && !data.groundIsFound()) {
                decision = echoTowards(currentDirection);
                scanned = false;
                fly = false;
                echoed = true;
            } else if (echoed && data.isFound()) {
                decision = fly();
                scanned = false;
                fly = true;
                outOfRangeCounter = 0;
                checkDistance = false;
            } else if (echoed && !data.isFound()) {
                gridSearchDistance = data.distance();
                decision = fly();

                if (gridSearchDistance < 4){
                    decision = scanGridSearch();
                    bigUTurnComplete = true;
                }
            }
        } else if (bigUTurnCounter == 0) {
            if (uTurnDirection == "right") {
                decision = echoLeft(currentDirection);
                this.temporaryDirection = this.currentDirection.left();
            } else if (uTurnDirection == "left") {
                decision = echoRight(currentDirection);
                this.temporaryDirection = this.currentDirection.right();
            }
            bigUTurnCounter++;
        } else if (bigUTurnCounter >= 1) {
            bigUTurnCounter++;
            if (distanceToLand <= 2) {
                if (bigUTurnCounter % 2 == 0) {
                    decision = fly();
                    distanceToLand = data.distance();
                } else {
                    decision = echoTowards(temporaryDirection);
                }
            } else if (distanceToLand > 2) {
                if (uTurnDirection.equals("right")) {
                    if (uTurns == 0) {
                        decision = turnLeftGridSearch();
                        uTurns++;
                    } else if (uTurns == 1) {
                        decision = fly();
                        uTurns++;
                    } else if (uTurns == 2) {
                        decision = turnLeftGridSearch();
                        uTurns++;
                    } else if (uTurns == 3) {
                        decision = scanGridSearch();
                        scanned = true;
                        uTurnComplete = true;
                        fly = false;
                        uTurns = 0;
                        echoed = false;
                        uTurnDirection = "right";
                        exploreIsland = false;
                        firstRowScan = true;
                    }
                } else if (uTurnDirection.equals("left")) {
                    if (uTurns == 0) {
                        uTurnComplete = false;
                        decision = turnRightGridSearch();
                        uTurns++;
                    } else if (uTurns == 1) {
                        decision = fly();
                        uTurns++;
                    } else if (uTurns == 2) {
                        decision = turnRightGridSearch();
                        uTurns++;
                    } else if (uTurns == 3) {
                        decision = scanGridSearch();
                        scanned = true;
                        uTurnComplete = true;
                        fly = false;
                        uTurns = 0;
                        echoed = false;
                        uTurnDirection = "left";
                        exploreIsland = false;
                        firstRowScan = true;
                    }
                }
            }
        }
        return decision;
    }

}
