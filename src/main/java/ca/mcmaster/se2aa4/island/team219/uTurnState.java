package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class uTurnState implements State {

    private Information info = new Information(0, new JSONObject());
    private AcknowledgeResults data = new AcknowledgeResults();

    @Override
    public JSONObject stateChange(GridSearch drone, Information currentInformation) {
        JSONObject decision = new JSONObject();

        this.info = currentInformation;
        data.initializeExtras(info);

        if (drone.uTurnDirection == "left"){
            if (drone.uTurns == 0) {
                decision = drone.turnLeftGridSearch();
                drone.uTurns++;
                drone.switchState(new uTurnState());
            } else if (drone.uTurns == 1) {
                decision = drone.turnLeftGridSearch();
                drone.uTurns++;
                drone.switchState(new uTurnState());
            } else if (drone.uTurns == 2) {
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
                drone.uTurns = 0;
                drone.uTurnDirection = "right";
                drone.switchState(new EchoState());
            }
        } else if (drone.uTurnDirection == "right"){
            if (drone.uTurns == 0) {
                decision = drone.turnRightGridSearch();
                drone.uTurns++;
                drone.switchState(new uTurnState());
            } else if (drone.uTurns == 1) {
                decision = drone.turnRightGridSearch();
                drone.uTurns++;
                drone.switchState(new uTurnState());
            } else if (drone.uTurns == 2) {
                String forward = drone.getCurrentDirection().toString();
                JSONObject forwardJ = new JSONObject().put("direction", forward);
                decision.put("action", "echo").put("parameters", forwardJ);
                drone.uTurns = 0;
                drone.uTurnDirection = "left";
                drone.switchState(new EchoState());
            }
        }
        drone.echoeUntilOcean = drone.uTurnDirection;
        
        return decision;
    } 
}
