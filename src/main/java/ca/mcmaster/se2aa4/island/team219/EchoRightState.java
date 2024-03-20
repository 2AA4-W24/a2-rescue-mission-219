package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class EchoRightState implements ToLandState {

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
            decision = drone.turnRightToLand();
        } else if (!data.isFound()) {
            if (data.distance() <= 10) {
                drone.dontEchoRight = true;
            }
            drone.switchState(new EchoLeftState());
            String forward = drone.getCurrentDirection().left().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);

            if (drone.dontEchoLeft) {
                drone.switchState(new FlyForwardState());
                decision.put("action", "fly");
            }

        }



        return decision;
    } 
}