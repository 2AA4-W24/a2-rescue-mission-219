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
    private int gridSearch;
    private boolean checkDistance;

    private boolean scanned;

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
        this.gridSearch = 0;
        this.checkDistance = false;
        this.map = new VirtualCoordinateMap(direction); // Initialize with current direction
        echo = new Echo();
        logger.info("created echo");
        this.uTurnDirection = "left";
        this.echoeUntilOcean = "left";
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
        } else {
            decision = scanLand();
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

        if (this.batteryLevel.batteryLevelLow()) { 
            decision = stop();
        
        } else if (echo.isFound()) {
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
                scanned = false;
                decision = fly();
                distanceToLand--;
            } else if (distanceToLand == 0 && scanned == false) {
                decision.put("action", "scan");
                scanned = true;
                missionToLand = true;
            }/* else if (scanned == true) {
                decision.put("action", "stop");
            } */
        }

        return decision;
    }

    public JSONObject scanLand() {
        JSONObject decision = new JSONObject();

        echo.initializeExtras(currentInformation);
        
        if (!uTurnComplete || (echoed && echo.outOfBounds())){
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
                }
            }
        } else if (scanned && !fly && echo.groundIsFound()) {
            decision = fly();
            scanned = false;
            fly = true;
        } else if (!scanned && fly && !echo.groundIsFound()) {
            decision = scan();
            scanned = true;
            fly = false;
            echoCounter = 0;
        /*} else if ( scanned && echo.groundIsFound()) {
            decision = fly();
            scanned = false;
            fly = true; */
        } else if ( scanned && !echo.groundIsFound()){
            decision = echoTowards(currentDirection);
            scanned = false;
            fly = false;
            echoed = true; 
        } else if ( echoed && echo.isFound()) {
            decision = fly();
            scanned = false;
            fly = true;
            
        } else if (echoed && !echo.isFound() && !checkDistance){
            if (echoeUntilOcean == "left"){
                decision = echoRight(currentDirection);
            } else if (echoeUntilOcean == "right"){
                decision = echoLeft(currentDirection);
            }
            checkDistance = true;
        } else if ( echoed && !echo.isFound() && echoCounter < 1 && checkDistance && echo.distance() < 2){
            decision = fly();
            checkDistance = false;
        } else if ( echoed && !echo.isFound() && echoCounter < 1 && checkDistance && echo.distance() >= 2){
            echoCounter++;
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
                }
            }
            echoeUntilOcean = uTurnDirection;
            checkDistance = false;
            
        } else if (echoCounter > 1) {
            //gridSearch++;
            decision = stop();
        }

        return decision;

        //if you visit y coordinate twice you stop
    }

}