package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class Translator {

    public Information translate(JSONObject response) {
        return new Information(response.getInt("cost"), response.getJSONObject("extras"));
    }

    public Turn translateDirection(String direction){

        Turn initialDirection = Turn.E;

        if (direction == "EAST"){
            initialDirection = Turn.E;
        } else if (direction == "WEST"){
            initialDirection = Turn.W;
        } else if (direction == "NORTH"){
            initialDirection = Turn.N;
        } else if (direction == "SOUTH"){
            initialDirection = Turn.S;
        }

        return initialDirection;
    }

}