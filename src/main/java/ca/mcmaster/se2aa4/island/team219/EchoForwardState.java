package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class EchoForwardState implements ToLandState {

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
            decision.put("action", "fly");
        } else if (!data.isFound()) {
            drone.switchState(new EchoRightState());
            String forward = drone.getCurrentDirection().right().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);

        }



        return decision;
    } 
}