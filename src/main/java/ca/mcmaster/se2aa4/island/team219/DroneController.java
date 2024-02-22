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
    private VirtualCoordinateMap map; //////

    private boolean scanned;

    private final Logger logger = LogManager.getLogger();


    public DroneController(Integer battery, Turn direction){
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
        this.map = new VirtualCoordinateMap(direction); // Initialize with current direction
        echo = new Echo();
        logger.info("created echo");
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
    public JSONObject echoInAllDirections() {

        currentDirection = Turn.E; //currentdirection is null when we start, fix that

        String forward = currentDirection.toString();
        String right = currentDirection.right().toString();
        String left = currentDirection.left().toString();

        JSONObject forwardJ = new JSONObject().put("direction", forward);
        JSONObject rightJ = new JSONObject().put("direction", right);
        JSONObject leftJ = new JSONObject().put("direction", left);
        //have to make all like this

        JSONObject decision = new JSONObject();
        
        if (!echoForward) {
            decision.put("action", "echo").put("parameters", forwardJ); // make echoTowards method to make this simpler
            this.echoForward = true;
            this.temporaryDirection = this.currentDirection;
        } else if (!echoRight) {
            decision.put("action", "echo").put("parameters", rightJ);
            this.echoRight = true;
            this.temporaryDirection = this.currentDirection.right();
        } else if (!echoLeft) {
            decision.put("action", "echo").put("parameters", leftJ);
            this.echoLeft = true;
            this.temporaryDirection = this.currentDirection.left();
        }
    
        if (echoForward && echoRight && echoLeft) {
            echoAll = true;
        } 

        return decision;
    }
    

    @Override
    public JSONObject makeDecision() {


        JSONObject decision = new JSONObject();

        echo.initializeExtras(currentInformation);

        if (this.batteryLevel.batteryLevelLow()) { 
            decision.put("action", "stop");
        
        } else if (echo.isFound()){
            distanceToLand = echo.distance();
            this.goToLand = true;
            decision = turn(this.temporaryDirection);

            map.turnRight();
            logger.info("Drone moved. Current position: " + map.getCurrentPosition());

        } else if (!echoAll && !goToLand && !echo.isFound()){
            decision = echoInAllDirections();

        } else if (echoAll && !goToLand) {
            decision.put("action", "fly");
            map.moveForward();
            logger.info("Drone moved. Current position: " + map.getCurrentPosition());
            echoAll = false;
            echoRight = false;
            echoLeft = false;

        } else if (goToLand){
            if (distanceToLand != 0){
                scanned = false;
                decision.put("action", "fly"); 
                map.moveForward();
                logger.info("Drone moved. Current position: " + map.getCurrentPosition());
                distanceToLand--;
            } else if (distanceToLand == 0 && scanned == false) {
                decision.put("action", "scan");
                scanned = true;
            } else if (scanned == true) {
                decision.put("action", "stop");
            }
        }

        return decision;
    }

    @Override
    public JSONObject turn(Turn direction){
        JSONObject decision = new JSONObject();
        currentDirection = direction;
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        return decision;
    }
    
    @Override
    public JSONObject turnLeft(){
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.left();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        return decision;
    }

    @Override
    public JSONObject turnRight(){
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.right();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        return decision;
    }

    @Override
    public JSONObject echoTowards(Turn direction){
        JSONObject decision = new JSONObject();
        return decision;
    }
}
