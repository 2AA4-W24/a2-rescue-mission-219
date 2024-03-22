package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class Commands {
    private String key = "action";
    private String value;
    private Compass currentDirection = Compass.noDirection;

    public Commands(String value) {
        this.value = value;
        //value can = stop, scan, fly
        
    }

    public Commands(String value, Compass currentDirection) {
        this.value = value;
        //if value = heading, turn
        //if value = echo, echo
        this.currentDirection = currentDirection;
    }

    public String getValue() {
        return this.value;
    }

    public Compass getDirection() {
        return this.currentDirection;
    }

    public JSONObject commandTranslator() {
        JSONObject decision = new JSONObject();
        if (currentDirection.toString().equals("noDirection")) {
            decision.put(key, value);
        } else {
            decision.put(key, value);
            decision.put("parameters", (new JSONObject()).put("direction", currentDirection.toString()));
        }
        return decision;
    }
}

