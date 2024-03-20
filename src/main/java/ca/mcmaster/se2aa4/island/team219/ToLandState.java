package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public interface ToLandState {

    public JSONObject stateChange(GoToLand drone, Information currentInformation);

}