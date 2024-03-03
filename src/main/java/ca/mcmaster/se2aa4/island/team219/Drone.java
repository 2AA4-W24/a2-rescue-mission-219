package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

import netscape.javascript.JSObject;


public interface Drone {
    void getInfo(Information info);
    int getBatteryLevelDrone();
    JSONObject makeDecision();
    JSONObject turn(Turn direction);
    JSONObject turnLeft();
    JSONObject turnRight();
    JSONObject echoInAllDirections();
    JSONObject echoTowards(Turn direction);
    JSONObject echoLeft(Turn direction);
    JSONObject echoRight(Turn direction);
    JSONObject toLand();
    JSONObject fly();
    JSONObject stop();
    JSONObject scan();
}