package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class TurnToLandState implements ToLandState {

    private Information info = new Information(0, new JSONObject());
    private AcknowledgeResults data = new AcknowledgeResults();

    @Override
    public JSONObject stateChange(GoToLand drone, Information currentInformation) {
        JSONObject decision = new JSONObject();
        this.info = currentInformation;
        data.initializeExtras(info);
        decision = drone.scanToLand();
        drone.missionToLand = true;
        return decision;
    } 

}