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
    private VirtualCoordinateMap map;
    private Turn oldDirection;
    
    
    //make decision only
    private boolean checkedForSite; 
    private boolean firstRun;// only used in make decision

    //toland & echoALL 
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
    private int distanceToLand;//shared between toLand and bigUturn, can be seperated
    

    //scanned should be true b4 this
    //gridsearch: i.e scanLand and BigUturn
    private boolean fly;
    private boolean echoed;
    private boolean uTurnComplete;
    private boolean bigUTurnComplete; // also used in make decision
    private int outOfRangeCounter;
    private int gridSearchDistance;
    private boolean checkDistance;
    private int bigUTurnCounter;
    private boolean exploreIsland; // also used in make decision
    private boolean passedLand; //Only used in scanLand
    private boolean echoedForward; //Only used in scanLand
    private int islandHalvesExplored; // //Only used in scanLand and make decision
    private int uTurns;
    private boolean firstRowScan;



    private boolean turnedRight;
    private boolean turnedLeft;

    public int emergencySiteCoordinatesX;
    public int emergencySiteCoordinatesY;
    public String creekCoordinates;
    private int creekX;
    private int creekY;

    private final Logger logger = LogManager.getLogger();

    public DroneController(Integer battery, Turn direction) {
        this.currentDirection = direction;
        int batteryInt = battery.intValue(); 
        this.batteryLevel = new BatteryLevel(batteryInt);
    
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
        this.map = new VirtualCoordinateMap(currentDirection); 
        echo = new Echo();
        logger.info("created echo");
        this.uTurnDirection = "left";
        this.echoeUntilOcean = "left";
        this.passedLand = false;
        this.echoedForward = false;
        this.islandHalvesExplored = 0;
        this.firstRowScan = false;
        this.checkedForSite = false;
        this.originalX = 0;
        this.originalY = 0;
        this.turnedLeft = false;
        this.turnedRight = false;

        this.emergencySiteCoordinatesX = 0;
        this.emergencySiteCoordinatesY = 0;
        this.creekCoordinates = "";
        this.creekX = 0;
        this.creekY = 0;
        

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

    public boolean batteryLevelWarning(){
        if (getBatteryLevelDrone() <= 30){
            return true;
        }else{
            return false;
        }
    }

    public String getClosestCreek(){
        return echo.calculateClosestCreek();
    }

    @Override
    public JSONObject makeDecision() {

        JSONObject decision = new JSONObject();

        echo.initializeExtras(currentInformation);

        if (checkedForSite) {
            if (echo.creekIsFound()) {
                creekX = map.getCurrentX();
                creekY = map.getCurrentY();
                echo.storeCoordinates(creekX,creekY);
                logger.info(map.getCurrentX()+map.getCurrentY());
            }
            if (echo.emergencySiteIsFound()) {
                emergencySiteCoordinatesX = map.getCurrentX();
                emergencySiteCoordinatesY = map.getCurrentY();
                echo.storeCoordinatesEmergency(emergencySiteCoordinatesX, emergencySiteCoordinatesY);
            }
            checkedForSite = false;
        }

        //logger.info(echo.calculateClosestCreek());
        logger.info(echo.getCreekIds());
        logger.info(echo.getCreekx());
        logger.info(echo.getCreeky());
        logger.info(echo.emergencyXss());
        logger.info(echo.emergencyYss());
        logger.info("current direction is " + currentDirection);
        logger.info("map direction is " + map.getDirection());

        logger.info("the battery level is "+ getBatteryLevelDrone());

        if (batteryLevelWarning()){
            decision = stop();
        } else if (firstRun){
            decision = echoInAllDirections();
            firstRun = false;
        } else if (!missionToLand) {
            decision = toLand();
        } else if (!exploreIsland && islandHalvesExplored == 0) {
            logger.info("Your doing first scan");
            decision = scanLand();
        } else if (!bigUTurnComplete && islandHalvesExplored == 1){
            logger.info("Your doing first U turn");
            //decision = stop();
            decision = bigUTurn();
        } else if (!exploreIsland && islandHalvesExplored == 1){
            logger.info("Your doing second scan");
            decision = scanLand();
        } else if (!bigUTurnComplete && islandHalvesExplored == 2){
            logger.info("Your doing second U turn");
            decision = bigUTurn();
        } else if (!exploreIsland && islandHalvesExplored == 2){
            decision = scanLand();
            //decision = stop();
        } else if (islandHalvesExplored == 3 || (originalX == map.getCurrentX() && originalY == map.getCurrentY())){
            decision = stop();
        }

        if (decision.toString().contains("fly")){
            map.moveForward();
            logger.info("Moved " + map.getCurrentPosition());
        } else if (decision.toString().contains("heading")){
            if (turnedRight == true){
                map.turnRight();
                turnedRight = false;
            } else if (turnedLeft == true){
                map.turnLeft();
                turnedLeft = false;
            }
            logger.info("Moved " + map.getCurrentPosition());
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
            turnedRight = true;
        } else if (currentDirection == oldDirection.left()) {
            map.turnLeft(); 
            logger.info("Turned left. New direction: " + map.getCurrentPosition());
            turnedLeft = true;
        }

        return decision;
    }

    @Override
    public JSONObject fly() {
        JSONObject decision = new JSONObject();
        decision.put("action", "fly");
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
        checkedForSite = true;
        return decision;
    }
    
    @Override
    public JSONObject turnLeft() {
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.left();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        turnedLeft = true;
        return decision;
    }

    @Override
    public JSONObject turnRight() {
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.right();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        turnedRight = true;
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

        if (echo.isFound() && echoCounter == 0) {
            distanceToLand = echo.distance();
            this.goToLand = true;
            if (currentDirection == temporaryDirection){
                decision = fly();
            }else{
                decision = turn(temporaryDirection);
            }
            
            if (temporaryDirection == currentDirection.left() || currentDirection == temporaryDirection){ 
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
                decision = scan();
                scanned = true;
                missionToLand = true;
                echoRight = false;
                echoLeft = false;
                originalX = map.getCurrentX();
                originalY = map.getCurrentY();
            }
        }

        return decision;
    }

    public JSONObject scanLand() {
        JSONObject decision = new JSONObject();

        echo.initializeExtras(currentInformation);

        if (echoed & echo.isFound() & outOfRangeCounter < 3){
            outOfRangeCounter = 0;
        }

        if (echoedForward == true && echo.isFound()){
            gridSearchDistance = echo.distance();
        }
        
        if (outOfRangeCounter >= 2 ){

            if (outOfRangeCounter == 2){
                fly = false;
                echoed = false;
                outOfRangeCounter ++;
            }

            if (fly & echo.isFound() & outOfRangeCounter < 20){
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
            } else if (echoed & (outOfRangeCounter >= 15 || !echo.isFound())){
                exploreIsland = true;
                decision = scan();
                bigUTurnComplete = false;
                outOfRangeCounter = 0;
                islandHalvesExplored++;
                logger.info("reached end of island");
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
            outOfRangeCounter = 0; 
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
            echoedForward = false;
            
        } else if (echoed && !echo.isFound() && !checkDistance){
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
        } else if (echo.isFound() ){
            decision = fly();
            checkDistance = false;
            
        }else if ( echoed && echo.isFound() && checkDistance){
            decision = fly();
            checkDistance = false;
        } else if ( echoed && !echo.isFound() && checkDistance){
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
        }
        
        return decision;
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
            } else if (echoed && echo.isFound()) {
                decision = fly();
                scanned = false;
                fly = true;
                outOfRangeCounter = 0;
                checkDistance = false;
            } else if (echoed && !echo.isFound()) {
                gridSearchDistance = echo.distance();
                decision = fly();

                if (gridSearchDistance < 4){
                    decision = scan();
                    bigUTurnComplete = true;
                }
            }
        } else if (bigUTurnCounter == 0) {
            if (uTurnDirection == "right") {
                logger.info("You made it here");
                decision = echoLeft(currentDirection);
                this.temporaryDirection = this.currentDirection.left();
            } else if (uTurnDirection == "left") {
                logger.info("You made it here");
                decision = echoRight(currentDirection);
                this.temporaryDirection = this.currentDirection.right();
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
        
        return decision;
    }
    
    

}