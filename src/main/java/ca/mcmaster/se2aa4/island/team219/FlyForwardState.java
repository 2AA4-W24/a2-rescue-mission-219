package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class FlyForwardState implements ToLandState {

    private Information info = new Information(0, new JSONObject());
    private AcknowledgeResults data = new AcknowledgeResults();

    @Override
    public JSONObject stateChange(GoToLand drone, Information currentInformation) {

        JSONObject decision = new JSONObject();
        this.info = currentInformation;
        data.initializeExtras(info);

        if (drone.dontEchoRight && !drone.dontEchoLeft) {
            drone.switchState(new EchoLeftState());
            String forward = drone.getCurrentDirection().left().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
        } else {
            drone.switchState(new EchoRightState());
            String forward = drone.getCurrentDirection().right().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
        }
        
        return decision;
    } 

}