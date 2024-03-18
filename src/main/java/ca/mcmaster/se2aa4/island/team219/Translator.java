package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class Translator {

    private Compass currentDirection; 

    public Information translate(JSONObject response) {
        return new Information(response.getInt("cost"), response.getJSONObject("extras"));
    }

    public Compass translateDirection(String direction){
    
        if (direction.equals("E")){
            currentDirection = Compass.E;
        } else if (direction.equals("W")){
            currentDirection = Compass.W;
        } else if (direction.equals("N")){
            currentDirection = Compass.N;
        } else if (direction.equals("S")){
            currentDirection = Compass.S;
        }
    
        return currentDirection;
    }
    
}