package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class Translator {

    public Information translate(JSONObject response) {
        return new Information(response.getInt("cost"), response.getJSONObject("extras"));
    }
    
}
