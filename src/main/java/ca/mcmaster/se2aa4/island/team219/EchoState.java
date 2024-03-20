package ca.mcmaster.se2aa4.island.team219;

import org.json.JSONObject;

public class EchoState implements State {

    private AcknowledgeResults data = new AcknowledgeResults();
    private Information info = new Information(0, new JSONObject());

    @Override
    public JSONObject stateChange(GridSearch drone, Information currentInformation) {
        
        JSONObject decision = new JSONObject();
        this.info = currentInformation;
        data.initializeExtras(info);
        
        if (data.isFound()) {
            drone.switchState(new FlyState());
            decision = decision.put("action", "fly");
            int distance = drone.distanceUntilLand();
            distance = distance - 1;
            drone.setRange(distance);
            drone.outOfRangeCounter= 0;
        } else if (!data.isFound()) {
            String forward = drone.getCurrentDirection().toString();
            JSONObject forwardJ = new JSONObject().put("direction", forward);
            decision.put("action", "echo").put("parameters", forwardJ);
            drone.outOfRangeCounter++;
            drone.switchState(new SecondEchoState());
        }
        
        return decision;
    } 

}
