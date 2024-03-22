package ca.mcmaster.se2aa4.island.team219;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Explorer implements IExplorerRaid {

    private Translator translator = new Translator();
    private final Logger logger = LogManager.getLogger();
    private Compass currentDirection; 
    private Drone rescueDrone;
    private HeadingTranslator headingTranslator = new HeadingTranslator();
    public JSONObject extras;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
        currentDirection = headingTranslator.translateDirection(direction);
        rescueDrone = new Drone(batteryLevel, currentDirection);
        logger.info("finished initializing");
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        Information info = translator.translate(response);
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        extras = extraInfo; 
        logger.info("Additional information received: {}", extraInfo); 
        //Drone.getInfo(info);
        rescueDrone.getInfo(info);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        //Commands command = Drone.makeDecision();
        Commands command = rescueDrone.makeDecision();
        decision = command.commandTranslator();
        logger.info("The new battery level is " + rescueDrone.getBatteryLevelDrone());
        logger.info(decision.toString());
        return decision.toString();
    }

    @Override
    public String deliverFinalReport() {
        logger.info("Closest creek ID: " + rescueDrone.getClosestCreek());
        return rescueDrone.getClosestCreek();
    }

}