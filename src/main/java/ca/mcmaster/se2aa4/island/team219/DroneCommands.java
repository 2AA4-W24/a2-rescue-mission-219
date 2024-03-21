package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public interface DroneCommands {
    JSONObject makeDecision();
    JSONObject turnLeft(Compass direction);
    JSONObject turnRight(Compass direction);
    JSONObject echoTowards(Compass direction);
    JSONObject echoLeft(Compass direction);
    JSONObject echoRight(Compass direction);
    JSONObject fly();
    JSONObject stop();
    JSONObject scan();
    void getInfo(Information info);
}