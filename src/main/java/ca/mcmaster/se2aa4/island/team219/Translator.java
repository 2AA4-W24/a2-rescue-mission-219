package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class Translator {

    private Turn currentDirection; 

    public Information translate(JSONObject response) {
        return new Information(response.getInt("cost"), response.getJSONObject("extras"));
    }

    public Turn translateDirection(String direction){
    
        if (direction.equals("E")){
            currentDirection = Turn.E;
        } else if (direction.equals("W")){
            currentDirection = Turn.W;
        } else if (direction.equals("N")){
            currentDirection = Turn.N;
        } else if (direction.equals("S")){
            currentDirection = Turn.S;
        }
    
        return currentDirection;
    }
    
}