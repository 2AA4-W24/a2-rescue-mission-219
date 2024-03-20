package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class FlyToUTurnState implements State {

    private Information info = new Information(0, new JSONObject());
    private AcknowledgeResults data = new AcknowledgeResults();

    @Override
    public JSONObject stateChange(GridSearch drone, Information currentInformation) {

        JSONObject decision = new JSONObject();

        this.info = currentInformation;
        data.initializeExtras(info);

        if (drone.distanceToOOB == 0) {
            drone.distanceToOOB = data.distance();
            decision = decision.put("action", "fly");
            drone.switchState(new FlyToUTurnState());
            drone.distanceToOOB--;
        } else if (drone.distanceToOOB > 7) {
            decision = decision.put("action", "fly");
            drone.switchState(new FlyToUTurnState());
            drone.distanceToOOB--;
        } else if (drone.distanceToOOB <= 7) {
            decision = decision.put("action", "fly");
            drone.switchState(new uTurnState());
            drone.distanceToOOB = 0;
        }

        if (drone.distanceToOOB < 2) {
            String forward = drone.getCurrentDirection().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
            drone.switchState(new uTurnState());
            drone.distanceToOOB = 0;
        }
        
        return decision;
    } 
}
