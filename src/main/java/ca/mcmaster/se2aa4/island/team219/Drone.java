package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public abstract class Drone implements DroneCommands {

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
        return decision;
    }
    
    @Override
    public JSONObject turnLeft(Compass currentDirection) {
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.left();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        return decision;
    }

    @Override
    public JSONObject turnRight(Compass currentDirection) {
        JSONObject decision = new JSONObject();
        currentDirection = currentDirection.right();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", currentDirection.toString()));
        return decision;
    }

    @Override
    public JSONObject echoTowards(Compass direction) {
        JSONObject decision = new JSONObject();
        String forward = direction.toString();
        JSONObject forwardJ = new JSONObject().put("direction", forward);
        decision.put("action", "echo").put("parameters", forwardJ);
        return decision;
    }

    @Override
    public JSONObject echoLeft(Compass direction) {
        JSONObject decision = new JSONObject();
        String left = direction.left().toString();
        JSONObject leftJ = new JSONObject().put("direction", left);
        decision.put("action", "echo").put("parameters", leftJ);
        return decision;
    }

    @Override
    public JSONObject echoRight(Compass direction) {
        JSONObject decision = new JSONObject();
        String right = direction.right().toString();
        JSONObject rightJ = new JSONObject().put("direction", right);
        decision.put("action", "echo").put("parameters", rightJ);
        return decision;
    }

}
