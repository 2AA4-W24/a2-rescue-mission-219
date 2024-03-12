package ca.mcmaster.se2aa4.island.team219;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class DroneController implements Drone {

    private Information currentInformation = new Information(0, new JSONObject());
    private Echo echo;
    private BatteryLevel batteryLevel; 
    private Turn currentDirection;
    private Turn temporaryDirection;
    private boolean echoAll;
    private boolean echoForward;
    private boolean echoRight;
    private boolean echoLeft;
    private int distanceToLand;
    private boolean goToLand;
    private boolean firstRun;
    private boolean uTurnComplete;
    private boolean fly;
    private boolean echoed;
    private int uTurns;
    private boolean missionToLand;
    private VirtualCoordinateMap map;
    private Turn oldDirection;
    private String uTurnDirection;
    private String echoeUntilOcean;
    private int echoCounter;
    private int outOfRangeCounter;
    private int gridSearchDistance;
    private boolean checkDistance;
    private int bigUTurnCounter;
    private boolean bigUTurnComplete;
    private boolean exploreIsland;
    private boolean passedLand;
    private boolean echoedForward;
    private boolean scanned;
    private int islandHalvesExplored;
    private boolean firstRowScan;

    private final Logger logger = LogManager.getLogger();

    public DroneController(Integer battery, Turn direction) {
        this.currentDirection = direction;
        logger.info("set direction");
        int batteryInt = battery.intValue(); 
        this.batteryLevel = new BatteryLevel(batteryInt);
        logger.info("set battery level");
        this.echoAll = false; 
        this.echoForward = false;
        this.echoLeft = false;
        this.echoRight = false; 
        this.goToLand = false;
        this.firstRun = true;
        this.uTurnComplete = true;
        this.missionToLand = false;
        this.fly = false;
        this.uTurns = 0;
        this.echoed = false;
        this.echoCounter = 0;
        this.exploreIsland = false;
        this.outOfRangeCounter = 0;
        this.gridSearchDistance = 0;
        this.bigUTurnCounter = 0;
        this.checkDistance = false;
        this.bigUTurnComplete = true;
        this.map = new VirtualCoordinateMap(direction); // Initialize with current direction
        echo = new Echo();
        logger.info("created echo");
        this.uTurnDirection = "left";
        this.echoeUntilOcean = "left";
        this.passedLand = false;
        this.echoedForward = false;
        this.islandHalvesExplored = 0;
        this.firstRowScan = false;
    }

    @Override
    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }

    @Override
    public int getBatteryLevelDrone() {
        return this.batteryLevel.getBatteryLevel();
    }

    @Override
    public JSONObject makeDecision() {

        JSONObject decision = new JSONObject();

        if (firstRun){
            decision = echoInAllDirections();
            firstRun = false;
        } else if (!missionToLand) {
            decision = toLand();
        } else if (!exploreIsland && islandHalvesExplored == 0) {
            decision = scanLand();
        } else if (!bigUTurnComplete && islandHalvesExplored == 1){
            //decision = stop();
            decision = bigUTurn();
        } else if (!exploreIsland && islandHalvesExplored == 1){
            decision = scanLand();
        } else if (islandHalvesExplored == 2){
            //decision = bigUTurn();
            decision = stop(); //instead of this make UTurn to original coordinates
        } else if (bigUTurnComplete & islandHalvesExplored == 2){
            decision = stop();
        }
        
        return decision;
    }

    @Override
    public JSONObject turn(Turn direction) {
        JSONObject decision = new JSONObject();
        oldDirection = currentDirection;
        currentDirection = direction;
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        
        if (currentDirection == oldDirection.right()) { 
            map.turnRight(); 
            logger.info("Turned right. New direction: " + map.getCurrentPosition());
        } else if (currentDirection == oldDirection.left()) {
            map.turnLeft(); 
            logger.info("Turned left. New direction: " + map.getCurrentPosition());
        }

        return decision;
    }

    @Override
    public JSONObject fly() {
        JSONObject decision = new JSONObject();
        decision.put("action", "fly");
        map.moveForward();
        logger.info("Moved forward. New direction: " + map.getCurrentPosition());
        return decision;
    }

    @Override
    public JSONObject stop() {
        JSONObject decision = new JSONObject();
        decision.put("action", "stop");
        return decision;
    }

    @Override
    public JSONObject scan() {
        JSONObject decision = new JSONObject();
        decision.put("action", "scan");
        return decision;
    }
    
    @Override
    public JSONObject turnLeft() {
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.left();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        map.turnLeft(); 
        logger.info("Turned left. New direction: " + map.getCurrentPosition());
        return decision;
    }

    @Override
    public JSONObject turnRight() {
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.right();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        map.turnRight(); 
        logger.info("Turned right. New direction: " + map.getCurrentPosition());
        return decision;
    }

    @Override
    public JSONObject echoTowards(Turn direction) {
        JSONObject decision = new JSONObject();
        String forward = direction.toString();
        JSONObject forwardJ = new JSONObject().put("direction", forward);
        decision.put("action", "echo").put("parameters", forwardJ);
        return decision;
    }

    @Override
    public JSONObject echoLeft(Turn direction) {
        JSONObject decision = new JSONObject();
        String left = direction.left().toString();
        JSONObject leftJ = new JSONObject().put("direction", left);
        decision.put("action", "echo").put("parameters", leftJ);
        return decision;
    }

    @Override
    public JSONObject echoRight(Turn direction) {
        JSONObject decision = new JSONObject();
        String right = direction.right().toString();
        JSONObject rightJ = new JSONObject().put("direction", right);
        decision.put("action", "echo").put("parameters", rightJ);
        return decision;
    }

    @Override
    public JSONObject echoInAllDirections() {

        currentDirection = Turn.E; //currentdirection is null when we start, fix that

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

    @Override
    public JSONObject toLand() {
        JSONObject decision = new JSONObject();

        echo.initializeExtras(currentInformation);

        if (echoCounter == 1){
            distanceToLand = echo.distance();
        }

        if (this.batteryLevel.batteryLevelLow()) { 
            decision = stop();
        
        } else if (echo.isFound() && echoCounter == 0) {
            distanceToLand = echo.distance();
            this.goToLand = true;
            decision = turn(temporaryDirection);
            if (temporaryDirection == currentDirection.left()){
                uTurnDirection = "right";
                echoeUntilOcean = "left";
            }else{
                uTurnDirection = "left";
                echoeUntilOcean = "right";
            }

        } else if (!echoAll && !goToLand && !echo.isFound()){
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
                decision.put("action", "scan");
                scanned = true;
                missionToLand = true;
                echoRight = false;
                echoLeft = false;
            }/* else if (scanned == true) {
                decision.put("action", "stop");
            } */
        }

        return decision;
    }

    public JSONObject scanLand() {
        JSONObject decision = new JSONObject();

        echo.initializeExtras(currentInformation);

        if (echoed & echo.isFound()){
            outOfRangeCounter = 0;
        }

        if (echoedForward == true && echo.isFound()){
            gridSearchDistance = echo.distance();
        }
        
        if (outOfRangeCounter == 2){
            exploreIsland = true;
            decision = echoRight(currentDirection);
            bigUTurnComplete = false;
            outOfRangeCounter = 0;
            islandHalvesExplored++;
            logger.info("reached end of island");

        } else if ( gridSearchDistance != 0){
            scanned = false;
            fly = true;
            outOfRangeCounter = 0;
            checkDistance = false;
            echoLeft = false;
            echoRight = false;
            echoedForward = false;
            decision = fly();
            gridSearchDistance--;
        } else if (!uTurnComplete || (echoed && echo.outOfBounds())){
            if (uTurnDirection == "left"){
                if (uTurns == 0) {
                    uTurnComplete = false;
                    decision = turnLeft();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnLeft();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scan();
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
                    decision = turnRight();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnRight();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scan();
                    scanned = true;
                    uTurnComplete = true;
                    fly = false;
                    uTurns = 0;
                    echoed = false;
                    uTurnDirection = "left";
                    passedLand = false;
                }
            }
        } else if (scanned && !fly && echo.groundIsFound()) {
            decision = fly();
            scanned = false;
            fly = true;
            checkDistance = false;
            echoLeft = false;
            echoRight = false;
        } else if (!scanned && fly && !echo.groundIsFound() &&!passedLand) {
            decision = scan();
            scanned = true;
            fly = false;
        
        } else if (scanned && !echo.groundIsFound()){
            decision = echoTowards(currentDirection);
            scanned = false;
            fly = false;
            echoed = true; 
            echoedForward = true;
        } else if (echoed && echo.isFound()) {
            decision = fly();
            scanned = false;
            fly = true;
            outOfRangeCounter = 0;
            checkDistance = false;
            echoLeft = false;
            echoRight = false;
            echoedForward = false;
            
        } else if (echoed && !echo.isFound() && !checkDistance){
            if (islandHalvesExplored % 2 == 0){
                if (echoeUntilOcean == "left"){
                    decision = echoRight(currentDirection);
                    echoRight = true;
                } else if (echoeUntilOcean == "right"){
                    decision = echoLeft(currentDirection);
                    echoLeft = true;
                }
            } else {
                if (echoeUntilOcean == "left"){
                    decision = echoRight(currentDirection);
                    echoRight = true;
                } else if (echoeUntilOcean == "right"){
                    decision = echoLeft(currentDirection);
                    echoLeft = true;
                }
            }
            
            outOfRangeCounter++;
            checkDistance = true;
            passedLand = true;
            echoedForward = false;
        } else if ((echoLeft || echoRight) && echo.isFound() ){
            decision = fly();
            checkDistance = false;
            echoLeft = false;
            echoRight = false;
        }else if ( echoed && echo.isFound() && checkDistance && echo.distance() != 0 ){
            decision = fly();
            checkDistance = false;
            echoLeft = false;
            echoRight = false;
        } else if ( echoed && !echo.isFound() && checkDistance || (echoRight || echoLeft) ){
            if (uTurnDirection == "left"){
                if (uTurns == 0) {
                    uTurnComplete = false;
                    decision = turnLeft();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnLeft();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scan();
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
                    decision = turnRight();
                    uTurns++;
                } else if (uTurns == 1) {
                    decision = turnRight();
                    uTurns++;
                } else if (uTurns == 2) {
                    decision = scan();
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
            
        /* } else if (outOfRangeCounter > 1) {
            //gridSearch++;
            logger.info("The counter is " + outOfRangeCounter + "and the u turn direction is " + uTurnDirection);
            decision = echoTowards(currentDirection);
            exploreIsland = true;
            bigUTurnComplete = false;*/
        }
        logger.info("distance to land is " + gridSearchDistance);
        return decision;

        //if you visit y coordinate twice you stop
    }

    public JSONObject bigUTurn() {

        JSONObject decision = new JSONObject();
        echo.initializeExtras(currentInformation);

        if (gridSearchDistance <= 4){
            decision = fly();
            gridSearchDistance--;
        } else {
            decision = scan();
            bigUTurnComplete = true;
        }

        if (firstRowScan == true) {
            if (scanned && !fly && echo.groundIsFound()) {
                decision = fly();
                scanned = false;
                fly = true;
            } else if (!scanned && fly && !echo.groundIsFound()) {
                decision = scan();
                scanned = true;
                fly = false;
            } else if (scanned && !echo.groundIsFound()) {
                decision = echoTowards(currentDirection);
                scanned = false;
                fly = false;
                echoed = true;
                echoedForward = true;
            } else if (echoed && echo.isFound()) {
                decision = fly();
                scanned = false;
                fly = true;
                outOfRangeCounter = 0;
                checkDistance = false;
                echoLeft = false;
                echoRight = false;
                echoedForward = false;
            } else if (echoed && !echo.isFound()) {
                gridSearchDistance = echo.distance();
                decision = fly();
            }
        } else if (bigUTurnCounter == 0) {
            if (uTurnDirection == "right") {
                decision = echoLeft(currentDirection);
                this.temporaryDirection = this.currentDirection.left();
                echoLeft = true;
            } else if (uTurnDirection == "left") {
                decision = echoRight(currentDirection);
                this.temporaryDirection = this.currentDirection.right();
                echoRight = true;
            }
            bigUTurnCounter++;
        } else if (bigUTurnCounter >= 1) {
            bigUTurnCounter++;
            if (distanceToLand <= 2) {
                if (bigUTurnCounter % 2 == 0) {
                    decision = fly();
                    distanceToLand = echo.distance();
                } else {
                    decision = echoTowards(temporaryDirection);
                }
            } else if (distanceToLand > 2) {
                if (uTurnDirection.equals("right")) {
                    if (uTurns == 0) {
                        decision = turnLeft();
                        uTurns++;
                    } else if (uTurns == 1) {
                        decision = fly();
                        uTurns++;
                    } else if (uTurns == 2) {
                        decision = turnLeft();
                        uTurns++;
                    } else if (uTurns == 3) {
                        decision = scan();
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
                        decision = turnRight();
                        uTurns++;
                    } else if (uTurns == 1) {
                        decision = fly();
                        uTurns++;
                    } else if (uTurns == 2) {
                        decision = turnRight();
                        uTurns++;
                    } else if (uTurns == 3) {
                        decision = scan();
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
        //decision = stop();
        return decision;
    }
    
    

}