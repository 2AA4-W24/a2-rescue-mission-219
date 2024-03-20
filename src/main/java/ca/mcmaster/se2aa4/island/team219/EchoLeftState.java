package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class EchoLeftState implements ToLandState {

    private Information info = new Information(0, new JSONObject());
    private AcknowledgeResults data = new AcknowledgeResults();

    @Override
    public JSONObject stateChange(GoToLand drone, Information currentInformation) {

        JSONObject decision = new JSONObject();

        this.info = currentInformation;
        data.initializeExtras(info);

        if (data.isFound()) {
            drone.switchState(new TurnToLandState());
            drone.distanceToLand = data.distance();
            drone.distanceToLand--;
            decision = drone.turnLeftToLand();
        } else if (!data.isFound()) {
            if (data.distance() <= 10) {
                drone.dontEchoLeft = true;
            }
            drone.switchState(new FlyForwardState());
            decision.put("action", "fly");

        }



        return decision;
    } 
}