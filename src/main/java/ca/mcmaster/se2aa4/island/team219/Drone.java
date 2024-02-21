package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;


public interface Drone {
    void getInfo(Information info);
    JSONObject makeDecision();
    JSONObject turn(Turn direction);
    JSONObject turnLeft();
    JSONObject turnRight();
    JSONObject echoInAllDirections();
}
