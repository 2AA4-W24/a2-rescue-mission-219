package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class SecondEchoState implements State {

    private AcknowledgeResults data = new AcknowledgeResults();
    private Information info = new Information(0, new JSONObject());

    @Override
    public JSONObject stateChange(GridSearch drone, Information currentInformation) {
        
        JSONObject decision = new JSONObject();
        this.info = currentInformation;
        data.initializeExtras(info);
        
        if (drone.outOfRangeCounter == 2) {
            drone.switchState(new FlyToEndState());
            String forward = drone.getCurrentDirection().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
        } else {
            drone.switchState(new FlyToUTurnState());
            String forward = drone.getCurrentDirection().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
        }
        
        return decision;
    } 
    
}
