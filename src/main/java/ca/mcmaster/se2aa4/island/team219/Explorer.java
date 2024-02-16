package ca.mcmaster.se2aa4.island.team219;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Explorer implements IExplorerRaid {

    boolean fly = true;
    int num = 0;

    private final Logger logger = LogManager.getLogger();

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
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        
        int range = 0;

        if (fly && num < 51) {
            decision.put("action", "scan");
            fly = false;
        } else if (num < 51) {
            decision.put("action", "fly");
            fly = true;
        } else if (num == 51) {
            JSONObject parameters = new JSONObject().put("direction", "N");
            decision.put("action", "echo").put("parameters", parameters);
        } else if (num == 52) {
            JSONObject parameters = new JSONObject().put("direction", "E");
            decision.put("action", "echo").put("parameters", parameters);
        } else if (num == 53) {
            JSONObject parameters = new JSONObject().put("direction", "S");
            decision.put("action", "echo").put("parameters", parameters);
            range = extras.getInt("range");
            logger.info("Range after echoing: {}", range);
            decision.put("action", "heading").put("parameters", parameters);
        } else if (num > 53 && num <= (53 + range + 2)) {
            if ((num - 54) % 2 == 0) {
                decision.put("action", "fly");
            } else {
                decision.put("action", "scan");
            }
        } else {
            decision.put("action", "stop");
        }
        num++; 
        return decision.toString();
    }

    
    public int echoResponse(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        JSONObject extras = response.getJSONObject("extras");
        int range = 0;
        if (extras.has("range")) {
            range = extras.getInt("range");
        } else {
            range = -1;
        }
        return range;
    }

    public int getRange() {
        if (extras != null && extras.has("range")) {
            return extras.getInt("range");
        }
        return -1; 
    }
    



    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        extras = extraInfo; 
        logger.info("Additional information received: {}", extraInfo);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }

}